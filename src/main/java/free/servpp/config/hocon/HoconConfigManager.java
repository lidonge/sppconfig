package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfig;
import free.servpp.config.IConfigManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The HoconConfigManager class implements the IConfigManager interface,
 * providing functionality to manage and retrieve HOCON configurations
 * by either a unique identifier or a modifier. This class is responsible
 * for storing configurations in memory and associating them with specific IDs or modifiers.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public class HoconConfigManager implements IConfigManager {
    private static final String CONFIG_FILE_EXTENSION = ".conf";
    private static final String RESOURCES_DIR = ""; // Update this path if needed

    private String configType;
    private Map<String, IConfig> configsById = new HashMap<>();
    private Map<String, IConfig> configsByModifier = new HashMap<>();

    /**
     * Constructs a new HoconConfigManager with the specified configuration type.
     *
     * @param configType The type of configuration managed by this instance.
     */
    public HoconConfigManager(String configType) {
        this.configType = configType;
    }

    /**
     * Retrieves the type of configuration managed by this instance.
     *
     * @return A string representing the configuration type.
     */
    @Override
    public String getConfigType() {
        return configType;
    }

    /**
     * Adds a configuration to the manager, associated with a specific modifier.
     * The configuration can later be retrieved using this modifier.
     *
     * @param modifier The modifier key used to associate with the configuration.
     * @param config   The configuration to be added.
     */
    @Override
    public void addConfigByModifier(String modifier, IConfig config) {
        configsByModifier.put(modifier, config);
    }

    /**
     * Adds a configuration to the manager, associated with a specific ID.
     * The configuration can later be retrieved using this ID.
     *
     * @param configId The ID used to associate with the configuration.
     * @param config   The configuration to be added.
     */
    @Override
    public void addConfigById(String configId, IConfig config) {
        configsById.put(configId, config);
    }

    /**
     * Retrieves a configuration associated with a specific ID.
     *
     * @param configId The ID of the configuration to retrieve.
     * @return The configuration associated with the given ID, or null if not found.
     */
    @Override
    public IConfig getConfigById(String configId) {
        return configsById.get(configId);
    }

    /**
     * Retrieves a configuration associated with a specific modifier.
     *
     * @param modifier The modifier of the configuration to retrieve.
     * @return The configuration associated with the given modifier, or null if not found.
     */
    @Override
    public IConfig getConfigByModifier(String modifier) {
        return configsByModifier.get(modifier);
    }
}