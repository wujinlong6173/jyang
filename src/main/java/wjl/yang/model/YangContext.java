package wjl.yang.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    /**
     * 根据名称和版本号找一个匹配的模块
     *
     * @param name 模块名
     * @param version 如果没有指定版本号则随便找一个版本
     * @return 找到的模块或空
     */
    public YangMainModule matchMainModule(String name, String version) {
        for (YangMainModule module : mainModules.values()) {
            if (Objects.equals(name, module.getName())) {
                if (version == null || Objects.equals(version, module.getVersion())) {
                    return module;
                }
            }
        }
        return null;
    }

    public void addSubModule(YangSubModule subModule) {
        ModuleNameVersion key = new ModuleNameVersion(subModule.getName(), subModule.getVersion());
        subModules.put(key, subModule);
    }

    public YangSubModule matchSubModule(String name, String version) {
        for (YangSubModule module : subModules.values()) {
            if (Objects.equals(name, module.getName())) {
                if (version == null || Objects.equals(version, module.getVersion())) {
                    return module;
                }
            }
        }
        return null;
    }
}
