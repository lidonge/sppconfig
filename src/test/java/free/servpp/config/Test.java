package free.servpp.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import free.servpp.config.hocon.HoconConfigLoader;
import free.servpp.config.hocon.HoconConfigTypeManager;
import free.servpp.config.hocon.IConfigurableBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public class Test {
    public static void main(String[] args) throws IOException {
        HoconConfigLoader loader = new HoconConfigLoader(".conf", "config", new IConfigurableBuilder() {
            @Override
            public IConfigurable build(String type, Config config) {
                if (type.equals("service")) {
                    return new IConfigurableForLoad() {
                        @Override
                        public String getConfigId() {
                            try {
                                return config.getString("service.serviceId");
                            }catch (ConfigException.Missing e){
                                return null;
                            }
                        }

                        @Override
                        public String getModifier() {
                            try {
                                return config.getString("service.modifier");
                            }catch (ConfigException.Missing e){
                                return null;
                            }
                        }

                        @Override
                        public List<String> getConfigIdList() {
                            try {
                                return config.getStringList("service.serviceId");
                            } catch (ConfigException.WrongType | ConfigException.Missing t) {
                                return null;
                            }
                        }

                        @Override
                        public List<String> getModifierList() {
                            try {
                                return config.getStringList("service.modifier");
                            } catch (ConfigException.WrongType | ConfigException.Missing t) {
                                return null;
                            }
                        }

                        @Override
                        public String getConfigType() {
                            return type;
                        }

                        @Override
                        public Config createConfigById(String id) {
                            String s = "service{serviceId=" + id + "}";
                            return ConfigFactory.parseReader(new StringReader(s));
                        }

                        @Override
                        public Config createConfigByModifier(String modifier) {
                            String s = "service{modifier=" + modifier + "}";
                            return ConfigFactory.parseReader(new StringReader(s));
                        }
                    };
                }
                return null;
            }
        });
        loader.load();
        HoconConfigTypeManager manager = loader.getManager();
        manager = manager;
    }
}
