package free.servpp.config.hocon;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import free.servpp.config.IConfigurable;
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

public class HoconConfigLoader {

    private String configFileExtension = ".conf";
    private String resourcesDir = "";

    private HoconConfigTypeManager manager;
    private IConfigurableBuilder builder;

    public HoconConfigLoader(String configFileExtension, String resourcesDir, IConfigurableBuilder builder) throws IOException {
        this.configFileExtension = configFileExtension;
        this.resourcesDir = resourcesDir;
        this.builder = builder;

        manager = new HoconConfigTypeManager().setBuilder(builder);
        load();
    }

    public HoconConfigTypeManager getManager() {
        return manager;
    }

    private void load() throws IOException {
        // Load all .conf files from the resources directory and its subdirectories
        List<Config> mergedConfig = loadConfigsFromResources(resourcesDir);
        Map<String, List<Config>> groupedByConfig = groupByRoot(mergedConfig);
        for(Map.Entry<String,List<Config>> entry:groupedByConfig.entrySet()) {
            String type = entry.getKey();
            List<Config> value = entry.getValue();
            List<Config> confs = new ArrayList<>();
            addConfigByModifiersToList(value, type, confs);
            addConfigByIdsToList(value, type, confs);
            if(confs.size() != 0)
                manager.addManagers(type, confs);
        }
    }
    private void addConfigByModifiersToList(List<Config> value, String type, List<Config> confs) {
        Map<String, Config> map = new HashMap<>();
        for(Config config: value){
            IConfigurableForLoad configurable = (IConfigurableForLoad) builder.build(type, config);
            if(configurable == null)
                continue;
            List<String> ids = configurable.getConfigIdList();
            List<String> modifiers = configurable.getModifierList();

            if(ids == null && modifiers == null){
                String id = configurable.getConfigId();
                String modifier = configurable.getModifier();

                if(id == null && modifier == null) {
                    if (map.get("*") != null)
                        throw new RuntimeException("Duplicate default config for " + type);
                    confs.add(config);
                    map.put("*", config);
                }else if(modifier != null){
                    if(map.get(modifier) != null)
                        throw new RuntimeException("Duplicate config for " + type +" with modifier " + modifier);
                    confs.add(config);
                    map.put(modifier,config);
                }
            }
            else if(modifiers != null){
                for(String modifier:modifiers){
                    config = configurable.createConfigByModifier(modifier).withFallback(config);
                    if(map.get(modifier) != null)
                        throw new RuntimeException("Duplicate config for " + type +" with modifier " + modifier);
                    confs.add(config);
                    map.put(modifier,config);
                }
            }
        }
    }

    private void addConfigByIdsToList(List<Config> value, String type, List<Config> confs) {
        Map<String, Config> map = new HashMap<>();
        for(Config config: value){
            IConfigurableForLoad configurable = (IConfigurableForLoad) builder.build(type, config);
            if(configurable == null)
                continue;
            List<String> ids = configurable.getConfigIdList();
            List<String> modifiers = configurable.getModifierList();
            if(ids == null && modifiers == null){
                String id = configurable.getConfigId();
                if(id != null) {
                    if(map.get(id) != null)
                        throw new RuntimeException("Duplicate config for " + type +" with id " + id);
                    confs.add(config);
                    map.put(id, config);
                }
            }
            else if(ids != null){
                for(String id:ids){
                    config = configurable.createConfigById(id).withFallback(config);
                    if(map.get(id) != null)
                        throw new RuntimeException("Duplicate config for " + type +" with id " + id);
                    confs.add(config);
                    map.put(id, config);
                }
            }
        }
    }

    private Map<String, List<Config>> groupByRoot(List<Config> mergedConfig) {
        Map<String, List<Config>> groupedByConfig = mergedConfig.stream()
                .collect(Collectors.groupingBy(new Function<Config, String>() {
                    @Override
                    public String apply(Config config) {
                        return config.root().entrySet().iterator().next().getKey();
                    }
                }));
        return groupedByConfig;
    }

    public List<Config> loadConfigsFromResources(String resourceDir) throws IOException {
        List<Config> configs = new ArrayList<>();

        // Load all .conf files from the resources directory and its subdirectories
        loadConfigsFromResourceDirectory(resourceDir, configs);

        return configs;
    }

    private void loadConfigsFromResourceDirectory(String resourceDir, List<Config> configs) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = classLoader.getResources(resourceDir);

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if ("file".equals(url.getProtocol())) {
                loadConfigsFromFileSystem(url, configs);
            } else if ("jar".equals(url.getProtocol())) {
//                loadConfigsFromJar(url, resourceDir, configs);
            }
        }
    }

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

    private void loadConfigFile(File file, List<Config> configs) {
        Config config = ConfigFactory.parseFile(file);
        configs.add(config);
    }
}