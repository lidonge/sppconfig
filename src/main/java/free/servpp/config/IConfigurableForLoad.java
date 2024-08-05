package free.servpp.config;

import com.typesafe.config.Config;

import java.util.List;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public interface IConfigurableForLoad extends IConfigurable{

    List<String> getConfigIdList();

    List<String> getModifierList();

    String getConfigType();

    Config createConfigById(String id);

    Config createConfigByModifier(String modifier);

}
