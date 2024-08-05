package free.servpp.config;

/**
 * @author lidong@date 2024-08-02@version 1.0
 */
public interface IConfig<T> {
    boolean isMerged();

    void mergeSuper(IConfig config);

    void setMerged(boolean b);

    T getConfigObject();
}
