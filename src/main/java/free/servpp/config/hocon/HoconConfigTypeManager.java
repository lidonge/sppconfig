package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfigManager;
import free.servpp.config.IConfigurable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HoconConfigTypeManager class is responsible for managing multiple
 * HOCON configuration managers, each associated with a different configuration
 * type. It allows adding configurations to these managers and building them
 * using a configurable builder.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public class HoconConfigTypeManager {
    // A map storing HoconConfigManager instances, keyed by their configuration type.
    private Map<String, HoconConfigManager> managerMap = new HashMap<>();

    // The builder used to create IConfigurable instances from a given type and Config.
    private IConfigurableBuilder builder;

    /**
     * Sets the builder used to create IConfigurable instances.
     *
     * @param builder The builder to use for creating configurable objects.
     * @return The current instance of HoconConfigTypeManager for method chaining.
     */
    public HoconConfigTypeManager setBuilder(IConfigurableBuilder builder) {
        this.builder = builder;
        return this;
    }

    /**
     * Adds a list of configuration objects to the manager associated with the specified type.
     * This method retrieves or creates the appropriate HoconConfigManager for the type
     * and builds configurations to be managed.
     *
     * @param type The type of configuration to add.
     * @param managerList A list of Config objects to be added to the manager.
     */
    public void addManagers(String type, List<Config> managerList) {
        HoconConfigManager manager = getHoconConfigManager(type);
        for (Config config : managerList) {
            buildHoconConfig(type, config, manager);
        }
    }

    /**
     * Builds and adds a HoconConfig to the provided manager. The configuration is
     * created based on the type and the given Config object. The configuration is
     * then associated with either a config ID or a modifier in the manager.
     *
     * @param type The type of configuration being built.
     * @param config The Config object to be used in creating the HoconConfig.
     * @param manager The HoconConfigManager where the configuration will be added.
     */
    private void buildHoconConfig(String type, Config config, HoconConfigManager manager) {
        IConfigurable configurable = builder.build(type, config);
        String configId = configurable.getConfigId();
        String modifier = configurable.getModifier();
        HoconConfigImpl hoconConfig = new HoconConfigImpl(config);

        if (configId != null) {
            manager.addConfigById(configId, hoconConfig);
        } else if (modifier != null) {
            manager.addConfigByModifier(modifier, hoconConfig);
        } else {
            manager.addConfigByModifier(IConfigManager.DEFAULT, hoconConfig);
        }
    }

    /**
     * Retrieves the HoconConfigManager associated with the specified type.
     * If no manager exists for the type, a new one is created and added to the map.
     *
     * @param type The configuration type.
     * @return The HoconConfigManager instance associated with the type.
     */
    private HoconConfigManager getHoconConfigManager(String type) {
        HoconConfigManager manager = managerMap.get(type);

        if (manager == null) {
            synchronized (this) {
                manager = managerMap.get(type);
                if (manager == null) {
                    manager = new HoconConfigManager(type);
                    managerMap.put(type, manager);
                }
            }
        }

        return manager;
    }
}