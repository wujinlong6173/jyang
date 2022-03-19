package wjl.yang.model;

public class ModuleAndIdentify {
    private final YangModule module;
    private final String identify;

    public ModuleAndIdentify(YangModule module, String identify) {
        this.module = module;
        this.identify = identify;
    }

    public YangModule getModule() {
        return module;
    }

    public String getIdentify() {
        return identify;
    }
}
