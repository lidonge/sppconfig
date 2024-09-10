package free.servpp.config;

/**
 * The IConfig interface defines a generic contract for configuration objects.
 * It provides methods to manage and track the merging status of configurations,
 * as well as to retrieve the underlying configuration object of a specified type.
 *
 * @param <T> The type of the underlying configuration object managed by this interface.
 *
 * @author lidong
 * @date 2024-08-02
 * @version 1.0
 */
public interface IConfig<T> {

    /**
     * Checks if the configuration has been merged with any other configurations.
     *
     * @return true if the configuration is merged; false otherwise.
     */
    boolean isMerged();

    /**
     * Merges this configuration with another configuration.
     * This operation typically involves combining or overriding the properties of the current configuration
     * with those of the provided configuration.
     *
     * @param config The configuration to merge with this configuration.
     */
    void mergeSuper(IConfig config);

    /**
     * Sets the merged status of the configuration.
     * This method is used to indicate whether the configuration has been merged with others.
     *
     * @param b The boolean value representing the merged status (true if merged, false otherwise).
     */
    void setMerged(boolean b);

    /**
     * Retrieves the underlying configuration object managed by this interface.
     *
     * @return The configuration object of type T.
     */
    T getConfigObject();
}