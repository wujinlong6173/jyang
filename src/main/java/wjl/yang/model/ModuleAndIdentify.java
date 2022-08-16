package wjl.yang.model;

/**
 * 模块和标识符。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
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
