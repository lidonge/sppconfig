package free.servpp.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigMergeable;

import java.util.List;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public interface IConfigurable {
    String getConfigId();

    String getModifier();
}
