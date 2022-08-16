package wjl.yang.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 保存解析后的主模块和子模块。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangContext {
    private final Map<ModuleNameVersion, YangMainModule> mainModules = new HashMap<>();
    private final Map<ModuleNameVersion, YangSubModule> subModules = new HashMap<>();

    /**
     * 添加主模块，会覆盖名称和版本号都相同的旧数据。
     *
     * @param module 主模块
     * @return 返回被覆盖的旧数据或空
     */
    public YangMainModule addMainModule(YangMainModule module) {
        ModuleNameVersion key = new ModuleNameVersion(module.getName(), module.getVersion());
        return mainModules.put(key, module);
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

    /**
     * 添加子模块，会覆盖名称和版本号都相同的旧数据。
     *
     * @param subModule 子模块
     * @return 返回被覆盖的旧数据或空
     */
    public YangSubModule addSubModule(YangSubModule subModule) {
        ModuleNameVersion key = new ModuleNameVersion(subModule.getName(), subModule.getVersion());
        return subModules.put(key, subModule);
    }

    /**
     * 根据名称和版本查找匹配的子模块
     *
     * @param name 名称
     * @param version 版本或空
     * @return 匹配的子模块或空
     */
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

    /**
     * 返回所有的主模块
     *
     * @return 所有的主模块
     */
    public Collection<YangMainModule> getMainModules() {
        return mainModules.values();
    }
}
