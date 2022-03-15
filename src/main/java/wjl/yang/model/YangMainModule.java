package wjl.yang.model;

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
    }
}
