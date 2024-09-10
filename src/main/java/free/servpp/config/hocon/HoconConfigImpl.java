package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfig;

/**
 * The HoconConfigImpl class is an implementation of the IConfig interface
 * that manages configuration data using the Typesafe Config library (HOCON).
 * This class supports merging configurations and tracking the merged status.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public class HoconConfigImpl implements IConfig<Config> {
    private boolean merged;
    private Config config;

    /**
     * Constructs a new HoconConfigImpl with the provided HOCON configuration.
     *
     * @param config The HOCON configuration object to be managed by this instance.
     */
    public HoconConfigImpl(Config config) {
        this.config = config;
    }

    /**
     * Returns whether this configuration has been merged with another.
     *
     * @return true if the configuration has been merged; false otherwise.
     */
    @Override
    public boolean isMerged() {
        return merged;
    }

    /**
     * Merges the current configuration with another configuration.
     * This method uses HOCON's withFallback method to merge configurations,
     * combining the current configuration with the provided one.
     *
     * @param conf The configuration to merge with the current configuration.
     */
    @Override
    public void mergeSuper(IConfig conf) {
        this.config = config.withFallback(((HoconConfigImpl) conf).getConfigObject());
    }

    /**
     * Sets the merged status of this configuration.
     *
     * @param b The boolean value representing whether this configuration has been merged.
     */
    @Override
    public void setMerged(boolean b) {
        merged = b;
    }

    /**
     * Retrieves the underlying HOCON configuration object managed by this instance.
     *
     * @return The HOCON configuration object.
     */
    @Override
    public Config getConfigObject() {
        return config;
    }
}