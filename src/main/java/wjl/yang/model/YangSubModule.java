package wjl.yang.model;

public class YangSubModule extends YangModule {
    private final String belongTo;

    /**
     * 子模块
     *
     * @param name 子模块的名称
     * @param prefix 所属主模块的前缀
     * @param version 子模块的版本
     * @param belongTo 所属主模块的名称
     */
    public YangSubModule(String name, String prefix, String version, String belongTo) {
        super(name, prefix, version);
        this.belongTo = belongTo;
    }
}
