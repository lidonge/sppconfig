package free.servpp.config;

/**
 * The IConfigManager interface serves as a configuration manager,
 * providing functionality to manage and retrieve configuration objects
 * based on different identifiers (ID) or modifiers.
 * It defines basic operations for adding, retrieving, and merging configurations.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public interface IConfigManager {

    /**
     * The ConfigLevel enum represents the priority order when looking up configurations.
     */
    enum ConfigLevel {
        ID,        // Lookup configuration by ID
        MODIFIER,  // Lookup configuration by modifier
        DEFAULT    // Use default configuration
    }

    /**
     * Default modifier value, used when the modifier is null to lookup the default configuration.
     */
    static final String DEFAULT = "*";

    /**
     * Get the type identifier for the configuration.
     *
     * @return A string representing the type of the configuration.
     */
    String getConfigType();

    /**
     * Add a configuration to the manager based on the given IConfigurable object's ID or modifier.
     * If the ID exists, the configuration is added using the ID as the identifier; otherwise, the modifier is used.
     *
     * @param configurable An object containing configuration identifiers (ID or modifier).
     * @param config The configuration object to be added.
     */
    default void addConfig(IConfigurable configurable, IConfig config) {
        String configId = configurable.getConfigId();
        String modifier = configurable.getModifier();
        if (configId != null) {
            addConfigById(configId, config);
        } else {
            if (modifier != null)
                addConfigByModifier(modifier, config);
            else
                addConfigByModifier(DEFAULT, config);
        }
    }

    /**
     * Add a configuration object based on the modifier.
     *
     * @param modifier The modifier for the configuration.
     * @param config The configuration object to be added.
     */
    void addConfigByModifier(String modifier, IConfig config);

    /**
     * Add a configuration object based on the configuration ID.
     *
     * @param configId The unique identifier for the configuration.
     * @param config The configuration object to be added.
     */
    void addConfigById(String configId, IConfig config);

    /**
     * Retrieve a configuration based on the given IConfigurable object.
     * If the configuration is found by ID, modifier, or default modifier and it has not been merged,
     * the configuration will be merged accordingly.
     *
     * @param configurable An object containing configuration identifiers (ID or modifier).
     * @return The retrieved configuration object, or null if not found.
     */
    default IConfig getConfig(IConfigurable configurable) {
        IConfig ret = null;
        ConfigLevel level = ConfigLevel.ID;
        ret = getConfigById(configurable.getConfigId());

        if (ret == null) {
            String modifier = configurable.getModifier();
            if (modifier != null) {
                ret = getConfigByModifier(modifier);
                level = ConfigLevel.MODIFIER;
            }
            if (ret == null || modifier == null) {
                ret = getDefaultConfigByModifier();
                level = ConfigLevel.DEFAULT;
            }
        }
        if (ret != null && !ret.isMerged()) {
            mergeConfig(ret, configurable, level);
        }
        return ret;
    }

    /**
     * Retrieve a configuration object based on the configuration ID.
     *
     * @param configId The unique identifier for the configuration.
     * @return The corresponding configuration object, or null if not found.
     */
    IConfig getConfigById(String configId);

    /**
     * Retrieve the configuration object associated with the default modifier.
     *
     * @return The corresponding default configuration object, or null if not found.
     */
    default IConfig getDefaultConfigByModifier() {
        return getConfigByModifier(DEFAULT);
    }

    /**
     * Retrieve a configuration object based on the modifier.
     *
     * @param modifier The modifier for the configuration.
     * @return The corresponding configuration object, or null if not found.
     */
    IConfig getConfigByModifier(String modifier);

    /**
     * Merge a configuration object. Based on the configuration priority (ID, modifier, default),
     * merge the configuration object into the target configuration.
     *
     * @param ret The configuration object to be merged.
     * @param configurable An object containing configuration identifiers (ID or modifier).
     * @param level The current configuration priority level.
     */
    default void mergeConfig(IConfig ret, IConfigurable configurable, ConfigLevel level) {
        switch (level) {
            case ID:
                IConfig config;
                String modifier = configurable.getModifier();
                if (modifier != null) {
                    config = getConfigByModifier(modifier);
                    if (config != null) {
                        ret.mergeSuper(config);
                    }
                }
                config = getDefaultConfigByModifier();
                if (config != null) {
                    ret.mergeSuper(config);
                }
                break;
            case MODIFIER:
                config = getDefaultConfigByModifier();
                if (config != null) {
                    ret.mergeSuper(config);
                }
                break;
            case DEFAULT:
                break;
            default:
                throw new RuntimeException("Error config level " + level);
        }
        ret.setMerged(true);
    }
}