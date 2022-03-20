package wjl.yang.model;

import wjl.yang.utils.YangError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YangModule {
    private final String name;
    private final String prefix;
    private final String version;

    private final Map<String, YangMainModule> prefixToModule = new HashMap<>();
    private final List<YangSubModule> subModules = new ArrayList<>();
    private final List<YangError> errors = new ArrayList<>();
    private YangStmt stmt;

    /**
     * 模块
     *
     * @param name 模块的名称
     * @param prefix 前缀
     * @param version 可选的版本
     */
    public YangModule(String name, String prefix, String version) {
        this.name = name;
        this.prefix = prefix;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getVersion() {
        return version;
    }

    public void setStmt(YangStmt stmt) {
        this.stmt = stmt;
    }

    public YangStmt getStmt() {
        return stmt;
    }

    public void addError(YangStmt pos, String msg) {
        errors.add(new YangError(pos, msg));
    }

    public void addPrefix(String pre, YangMainModule module) {
        prefixToModule.put(pre, module);
    }

    public YangMainModule getPrefix(String pre) {
        return prefixToModule.get(pre);
    }

    public void addSubModule(YangSubModule sub) {
        subModules.add(sub);
    }

    public List<YangSubModule> getSubModules() {
        return subModules;
    }

    public ModuleAndIdentify separate(String prefixId) {
        int idx = prefixId.indexOf(':');
        if (idx < 0) {
            return new ModuleAndIdentify(this, prefixId);
        }
        String prefix = prefixId.substring(0, idx);
        YangModule module = prefixToModule.get(prefix);
        if (module == null) {
            return null;
        } else {
            return new ModuleAndIdentify(module, prefixId.substring(idx + 1));
        }
    }

    public List<YangError> getErrors() {
        return errors;
    }
}
