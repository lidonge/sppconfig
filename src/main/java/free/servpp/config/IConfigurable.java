package free.servpp.config;

/**
 * The IConfigurable interface defines the contract for objects that can be configured.
 * It provides methods to retrieve configuration identifiers such as a unique ID or a modifier.
 * These identifiers are used to manage and retrieve corresponding configuration objects.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public interface IConfigurable {

    /**
     * Retrieves the unique configuration ID for this object.
     * This ID is used to identify and associate the object with a specific configuration.
     *
     * @return A string representing the configuration ID, or null if not applicable.
     */
    String getConfigId();

    /**
     * Retrieves the modifier associated with this object.
     * The modifier is used as an alternative or additional identifier when retrieving configurations.
     *
     * @return A string representing the modifier, or null if not applicable.
     */
    String getModifier();
}