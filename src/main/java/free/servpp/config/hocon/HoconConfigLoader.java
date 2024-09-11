package free.servpp.config.hocon;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import free.servpp.config.IConfigurableForLoad;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * This class is responsible for loading configuration files in HOCON format from a specified resources directory.
 * It initializes a HoconConfigTypeManager to manage configurations and provides functionality to load and group configurations
 * based on their identifiers and modifiers.
 */
public class HoconConfigLoader {

    private String configFileExtension = ".conf";
    private String resourcesDir = "";
    private HoconConfigTypeManager manager;
    private IConfigurableBuilder builder;

    /**
     * Constructs a HoconConfigLoader with the specified configuration file extension, resources directory, and builder.
     *
     * @param configFileExtension the file extension for configuration files
     * @param resourcesDir        the directory from which to load configuration files
     * @param builder             the builder for creating configurable objects
     * @throws IOException if an error occurs while loading the configuration files
     */
    public HoconConfigLoader(String configFileExtension, String resourcesDir, IConfigurableBuilder builder) throws IOException {
        this.configFileExtension = configFileExtension;
        this.resourcesDir = resourcesDir;
        this.builder = builder;
        manager = new HoconConfigTypeManager().setBuilder(builder);
        load();
    }

    /**
     * Returns the configuration type manager.
     *
     * @return the HoconConfigTypeManager instance
     */
    public HoconConfigTypeManager getManager() {
        return manager;
    }

    /**
     * Loads configuration files from the specified resources directory,
     * groups them by their root keys, and adds them to the manager.
     *
     * @throws IOException if an error occurs while loading the configuration files
     */
    private void load() throws IOException {
        // Load all .conf files from the resources directory and its subdirectories
        List<Config> mergedConfig = loadConfigsFromResources(resourcesDir);
        Map<String, List<Config>> groupedByConfig = groupByRoot(mergedConfig);
        for (Map.Entry<String, List<Config>> entry : groupedByConfig.entrySet()) {
            String type = entry.getKey();
            List<Config> value = entry.getValue();
            List<Config> confs = new ArrayList<>();
            addConfigByModifiersToList(value, type, confs);
            addConfigByIdsToList(value, type, confs);
            if (confs.size() != 0)
                manager.addManagers(type, confs);
        }
    }

    /**
     * Adds configurations to the list based on their modifiers.
     *
     * @param value the list of configurations to process
     * @param type  the type of configuration
     * @param confs the list to which valid configurations will be added
     */
    private void addConfigByModifiersToList(List<Config> value, String type, List<Config> confs) {
        Map<String, Config> map = new HashMap<>();
        for (Config config : value) {
            IConfigurableForLoad configurable = (IConfigurableForLoad) builder.build(type, config);
            if (configurable == null)
                continue;
            List<String> ids = configurable.getConfigIdList();
            List<String> modifiers = configurable.getModifierList();

            if (ids == null && modifiers == null) {
                String id = configurable.getConfigId();
                String modifier = configurable.getModifier();

                if (id == null && modifier == null) {
                    if (map.get("*") != null)
                        throw new RuntimeException("Duplicate default config for " + type);
                    confs.add(config);
                    map.put("*", config);
                } else if (modifier != null) {
                    if (map.get(modifier) != null)
                        throw new RuntimeException("Duplicate config for " + type + " with modifier " + modifier);
                    confs.add(config);
                    map.put(modifier, config);
                }
            } else if (modifiers != null) {
                for (String modifier : modifiers) {
                    config = configurable.createConfigByModifier(modifier).withFallback(config);
                    if (map.get(modifier) != null)
                        throw new RuntimeException("Duplicate config for " + type + " with modifier " + modifier);
                    confs.add(config);
                    map.put(modifier, config);
                }
            }
        }
    }

    /**
     * Adds configurations to the list based on their IDs.
     *
     * @param value the list of configurations to process
     * @param type  the type of configuration
     * @param confs the list to which valid configurations will be added
     */
    private void addConfigByIdsToList(List<Config> value, String type, List<Config> confs) {
        Map<String, Config> map = new HashMap<>();
        for (Config config : value) {
            IConfigurableForLoad configurable = (IConfigurableForLoad) builder.build(type, config);
            if (configurable == null)
                continue;
            List<String> ids = configurable.getConfigIdList();
            List<String> modifiers = configurable.getModifierList();
            if (ids == null && modifiers == null) {
                String id = configurable.getConfigId();
                if (id != null) {
                    if (map.get(id) != null)
                        throw new RuntimeException("Duplicate config for " + type + " with id " + id);
                    confs.add(config);
                    map.put(id, config);
                }
            } else if (ids != null) {
                for (String id : ids) {
                    config = configurable.createConfigById(id).withFallback(config);
                    if (map.get(id) != null)
                        throw new RuntimeException("Duplicate config for " + type + " with id " + id);
                    confs.add(config);
                    map.put(id, config);
                }
            }
        }
    }

    /**
     * Groups configurations by their root keys.
     *
     * @param mergedConfig the list of merged configurations
     * @return a map where the keys are the root keys and the values are lists of configurations
     */
    private Map<String, List<Config>> groupByRoot(List<Config> mergedConfig) {
        return mergedConfig.stream()
                .collect(Collectors.groupingBy(new Function<Config, String>() {
                    @Override
                    public String apply(Config config) {
                        return config.root().entrySet().iterator().next().getKey();
                    }
                }));
    }

    /**
     * Loads configuration files from the specified resources directory.
     *
     * @param resourceDir the directory from which to load configuration files
     * @return a list of loaded configuration objects
     * @throws IOException if an error occurs while loading the configuration files
     */
    public List<Config> loadConfigsFromResources(String resourceDir) throws IOException {
        List<Config> configs = new ArrayList<>();
        loadConfigsFromResourceDirectory(resourceDir, configs);
        return configs;
    }

    /**
     * Loads configuration files from a specific resource directory.
     *
     * @param resourceDir the directory from which to load configuration files
     * @param configs     the list to which loaded configurations will be added
     * @throws IOException if an error occurs while loading the configuration files
     */
    private void loadConfigsFromResourceDirectory(String resourceDir, List<Config> configs) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = classLoader.getResources(resourceDir);

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if ("file".equals(url.getProtocol())) {
                loadConfigsFromFileSystem(url, configs);
            } else if ("jar".equals(url.getProtocol())) {
                // loadConfigsFromJar(url, resourceDir, configs);
            }
        }
    }

    /**
     * Loads configuration files from the file system.
     *
     * @param url     the URL pointing to the resources directory
     * @param configs the list to which loaded configurations will be added
     * @throws IOException if an error occurs while loading the configuration files
     */
    private void loadConfigsFromFileSystem(URL url, List<Config> configs) throws IOException {
        File file = new File(url.getFile());
        if (file.isDirectory()) {
            // Handle directory traversal for file system resources
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        loadConfigsFromFileSystem(f.toURL(), configs);
                    } else if (f.getName().endsWith(configFileExtension)) {
                        loadConfigFile(f, configs);
                    }
                }
            }
        } else {
            // Handle single file
            if (file.getName().endsWith(configFileExtension)) {
                loadConfigFile(file, configs);
            }
        }
    }

    /**
     * Loads configuration files from a JAR file.
     *
     * @param jarUrl      the URL pointing to the JAR file
     * @param resourceDir the directory within the JAR from which to load configuration files
     * @param configs     the list to which loaded configurations will be added
     * @throws IOException if an error occurs while loading the configuration files
     */
    private void loadConfigsFromJar(URL jarUrl, String resourceDir, List<Config> configs) throws IOException {
        try (JarFile jarFile = new JarFile(jarUrl.getFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(resourceDir) && entryName.endsWith(configFileExtension)) {
                    try (InputStream inputStream = jarFile.getInputStream(entry)) {
                        Config config = ConfigFactory.parseReader(new InputStreamReader(inputStream, "UTF-8"));
                        configs.add(config);
                    }
                }
            }
        }
    }

    /**
     * Loads a single configuration file and adds it to the provided list.
     *
     * @param file    the configuration file to load
     * @param configs the list to which the loaded configuration will be added
     */
    private void loadConfigFile(File file, List<Config> configs) {
        Config config = ConfigFactory.parseFile(file);
        configs.add(config);
    }
}