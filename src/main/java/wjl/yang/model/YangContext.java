package wjl.yang.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class YangContext {
    private final Map<ModuleNameVersion, YangMainModule> mainModules = new HashMap<>();
    private final Map<ModuleNameVersion, YangSubModule> subModules = new HashMap<>();

    public void addMainModule(YangMainModule module) {
        ModuleNameVersion key = new ModuleNameVersion(module.getName(), module.getVersion());
        mainModules.put(key, module);
    }

    public YangMainModule getMainModule(ModuleNameVersion key) {
        return mainModules.get(key);
    }

    public void addSubModule(YangSubModule subModule) {
        ModuleNameVersion key = new ModuleNameVersion(subModule.getName(), subModule.getVersion());
        subModules.put(key, subModule);
    }

    public YangSubModule getSubModule(ModuleNameVersion key) {
        return subModules.get(key);
    }
}
