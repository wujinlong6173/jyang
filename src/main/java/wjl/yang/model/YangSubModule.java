package wjl.yang.model;

public class YangSubModule extends YangModule {
    /**
     * 所属主模块的名称
     */
    private final String belongTo;

    /**
     * 有没有被主模块包含
     */
    private boolean includedByMain;

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

    public String getBelongTo() {
        return belongTo;
    }

    public void setIncludedByMain() {
        includedByMain = true;
    }

    public boolean isIncludedByMain() {
        return includedByMain;
    }

    @Override
    public boolean isMainModule() {
        return false;
    }
}
