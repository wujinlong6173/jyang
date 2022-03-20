package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;
import wjl.yang.utils.YangKeyword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理 grouping 和 uses 语句，将 uses 语句替换成 grouping 中的内容。
 */
class GroupingCompiler {
    /**
     *
     * @param modules 主模块和子模块。
     */
    static void collectGroupings(List<YangModule> modules) {
        // 搜索每个模块、子模块定义的预制组。
        Map<YangModule, Map<String, YangStmt>> moduleToGroupings = new HashMap<>();
        for (YangModule module : modules) {
            moduleToGroupings.put(module, searchOneModule(module));
        }

        // 复制子模块定义的预置组
        Map<YangModule, Map<String, YangStmt>> includedGroupings = new HashMap<>();
        for (YangModule module : modules) {
            includedGroupings.put(module, copyIncludedGrouping(module, moduleToGroupings));
        }
    }

    private static Map<String, YangStmt> searchOneModule(YangModule module) {
        Map<String, YangStmt> groupingMap = new HashMap<>();
        module.getStmt().forEach(YangKeyword.GROUPING, (group) -> {
            String name = group.getValue();
            YangStmt exist = groupingMap.get(name);
            if (exist != null) {
                module.addError(group, " is already defined in " + exist.toString());
            } else {
                groupingMap.put(name, group);
            }
        });
        return groupingMap;
    }

    private static Map<String, YangStmt> copyIncludedGrouping(YangModule module,
        Map<YangModule, Map<String, YangStmt>> moduleToGroupings) {
        Map<String, YangStmt> ret = new HashMap<>();
        Map<String, YangStmt> temp = moduleToGroupings.get(module);
        if (temp != null) {
            ret.putAll(temp);
        }

        for (YangSubModule sub : module.getSubModules()) {
            temp = moduleToGroupings.get(sub);
            if (temp != null) {
                for (YangStmt group : temp.values()) {
                    YangStmt exist = ret.get(group.getValue());
                    if (exist != null) {
                        module.addError(group, String.format(" is already defined in %s",
                            exist.toString()));
                    } else {
                        ret.put(group.getValue(), group);
                    }
                }
            }
        }
        return ret;
    }
}
