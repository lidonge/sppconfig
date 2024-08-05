package free.servpp.config;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public interface IConfigManager {
    enum ConfigLevel {
        ID, MODIFIER, DEFAULT
    }

    static final String DEFAULT = "*";

    String getConfigType();

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

    void addConfigByModifier(String modifier, IConfig config);

    void addConfigById(String configId, IConfig config);

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

    IConfig getConfigById(String configId);

    default IConfig getDefaultConfigByModifier() {
        return getConfigByModifier(DEFAULT);
    }

    IConfig getConfigByModifier(String modifier);

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
