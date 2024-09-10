package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfigurable;

/**
 * The IConfigurableBuilder interface defines a contract for building
 * IConfigurable objects. Implementations of this interface are responsible
 * for creating IConfigurable instances based on the provided configuration
 * type and Config object.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public interface IConfigurableBuilder {

    /**
     * Builds an IConfigurable instance based on the specified configuration
     * type and configuration data.
     *
     * @param type The type of configuration being built.
     * @param config The Config object containing the configuration data.
     * @return An IConfigurable instance constructed from the provided type and config.
     */
    IConfigurable build(String type, Config config);
}