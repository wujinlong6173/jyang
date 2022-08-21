package wjl.yang.model;

/**
 * 主模块。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangMainModule extends YangModule {
    /**
     * 主模块
     *
     * @param name 主模块的名称
     * @param prefix 主模块的前缀
     * @param version 主模块的版本
     */
    public YangMainModule(String name, String prefix, String version) {
        super(name, prefix, version);
        setMainModule(this);
    }
}
