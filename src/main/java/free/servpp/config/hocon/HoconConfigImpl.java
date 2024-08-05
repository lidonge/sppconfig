package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfig;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public class HoconConfigImpl implements IConfig<Config>{
    private boolean merged;
    private Config config;

    public HoconConfigImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean isMerged() {
        return merged;
    }

    @Override
    public void mergeSuper(IConfig conf) {
        this.config =  config.withFallback(((HoconConfigImpl)conf).getConfigObject());
    }

    @Override
    public void setMerged(boolean b) {
        merged = b;
    }

    @Override
    public Config getConfigObject() {
        return config;
    }
}
