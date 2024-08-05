package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfig;
import free.servpp.config.IConfigManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public class HoconConfigManager implements IConfigManager {
    private static final String CONFIG_FILE_EXTENSION = ".conf";
    private static final String RESOURCES_DIR = ""; // Update this path if needed

    private String configType;
    private Map<String, IConfig> configsById = new HashMap<>();
    private Map<String, IConfig> configsByModifier = new HashMap<>();

    public HoconConfigManager(String configType) {
        this.configType = configType;
    }

    @Override
    public String getConfigType() {
        return configType;
    }

    @Override
    public void addConfigByModifier(String modifier, IConfig config) {
        configsByModifier.put(modifier,config);
    }

    @Override
    public void addConfigById(String configId, IConfig config) {
        configsById.put(configId,config);
    }

    @Override
    public IConfig getConfigById(String configId) {
        return configsById.get(configId);
    }

    @Override
    public IConfig getConfigByModifier(String modifier) {
        return configsByModifier.get(modifier);
    }
}
