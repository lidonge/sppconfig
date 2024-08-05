package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfigManager;
import free.servpp.config.IConfigurable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public class HoconConfigTypeManager {
    private Map<String, HoconConfigManager> managerMap = new HashMap<>();
    private IConfigurableBuilder builder;

    public HoconConfigTypeManager setBuilder(IConfigurableBuilder builder) {
        this.builder = builder;
        return this;
    }

    public void addManagers(String type, List<Config> managerList){
        HoconConfigManager manager = getHoconConfigManager(type);
        for(Config config:managerList){
            buildHoconConfig(type, config, manager);
        }
    }

    private void buildHoconConfig(String type, Config config, HoconConfigManager manager) {
        IConfigurable configurable = builder.build(type, config);
        String configId = configurable.getConfigId();
        String modifier = configurable.getModifier();
        HoconConfigImpl hoconConfig = new HoconConfigImpl(config);
        if(configId != null) {
            manager.addConfigById(configId, hoconConfig);
        } else if (modifier != null) {
            manager.addConfigByModifier(modifier,hoconConfig);
        } else {
            manager.addConfigByModifier(IConfigManager.DEFAULT, hoconConfig);
        }
    }

    private HoconConfigManager getHoconConfigManager(String type) {
        HoconConfigManager manager = managerMap.get(type);
        if(manager == null){
            synchronized (this){
                manager = managerMap.get(type);
                if(manager == null){
                    manager = new HoconConfigManager(type);
                    managerMap.put(type,manager);
                }
            }
        }
        return manager;
    }
}
