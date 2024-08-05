package free.servpp.config.hocon;

import com.typesafe.config.Config;
import free.servpp.config.IConfigurable;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public interface IConfigurableBuilder {
    IConfigurable build(String type, Config config);
}
