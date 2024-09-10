package free.servpp.config;

import com.typesafe.config.Config;
import java.util.List;

/**
 * The IConfigurableForLoad interface extends the IConfigurable interface,
 * providing additional methods specifically for loading and managing configurations.
 * This interface allows for the retrieval of multiple configuration IDs and modifiers,
 * as well as the creation of configuration objects based on these identifiers.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public interface IConfigurableForLoad extends IConfigurable {

    /**
     * Retrieves a list of configuration IDs associated with this object.
     * These IDs are used to identify and load corresponding configuration objects.
     *
     * @return A list of strings representing the configuration IDs.
     */
    List<String> getConfigIdList();

    /**
     * Retrieves a list of modifiers associated with this object.
     * Modifiers are used as alternative or additional identifiers for configuration management.
     *
     * @return A list of strings representing the modifiers.
     */
    List<String> getModifierList();

    /**
     * Retrieves the type of configuration this object represents.
     * This type can be used to categorize or differentiate between different kinds of configurations.
     *
     * @return A string representing the configuration type.
     */
    String getConfigType();

    /**
     * Creates a configuration object based on a specific configuration ID.
     *
     * @param id The configuration ID used to create the configuration.
     * @return A Config object created based on the provided ID.
     */
    Config createConfigById(String id);

    /**
     * Creates a configuration object based on a specific modifier.
     *
     * @param modifier The modifier used to create the configuration.
     * @return A Config object created based on the provided modifier.
     */
    Config createConfigByModifier(String modifier);

}