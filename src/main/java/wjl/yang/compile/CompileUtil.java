package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;
import wjl.yang.utils.YangKeyword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class CompileUtil {
    static String version(YangStmt stmt) {
        YangStmt version = stmt.searchOne(YangKeyword.REVISION);
        return version != null ? version.getValue() : null;
    }

    static String prefix(YangStmt stmt) {
        YangStmt prefix = stmt.searchOne(YangKeyword.PREFIX);
        return prefix != null ? prefix.getValue() : null;
    }

    static String revisionDate(YangStmt stmt) {
        YangStmt ver = stmt.searchOne(YangKeyword.REVISION_DATE);
        return ver != null ? ver.getValue() : null;
    }

    /**
     * 收集模块、子模块中定义的特定语句，并复制子模块中的语句，根据名称检查重复定义。
     *
     * @param modules 主模块和子模块
     * @param key 要搜索的语句
     * @return 模块 > 名称 > 语句
     */
    static Map<YangModule, Map<String, YangStmt>> collectSpecialStmt(List<YangModule> modules, String key) {
        // 搜索每个模块、子模块中的指定语句。
        Map<YangModule, Map<String, YangStmt>> moduleToStmt = new HashMap<>();
        for (YangModule module : modules) {
            moduleToStmt.put(module, searchOneModule(module, key));
        }

        // 复制子模块定义的预置组
        Map<YangModule, Map<String, YangStmt>> includedStmt = new HashMap<>();
        for (YangModule module : modules) {
            includedStmt.put(module, copyIncludedStmt(module, moduleToStmt));
        }
        return includedStmt;
    }

    private static Map<String, YangStmt> searchOneModule(YangModule module, String key) {
        Map<String, YangStmt> stmtMap = new HashMap<>();
        module.getStmt().forEach(key, (group) -> {
            String name = group.getValue();
            YangStmt exist = stmtMap.get(name);
            if (exist != null) {
                module.addError(group, " is already defined in " + exist.toString());
            } else {
                stmtMap.put(name, group);
            }
        });
        return stmtMap;
    }

    private static Map<String, YangStmt> copyIncludedStmt(YangModule module,
        Map<YangModule, Map<String, YangStmt>> moduleToStmt) {
        Map<String, YangStmt> ret = new HashMap<>();
        Map<String, YangStmt> temp = moduleToStmt.get(module);
        if (temp != null) {
            ret.putAll(temp);
        }

        for (YangSubModule sub : module.getSubModules()) {
            temp = moduleToStmt.get(sub);
            if (temp != null) {
                for (YangStmt inc : temp.values()) {
                    YangStmt exist = ret.get(inc.getValue());
                    if (exist != null) {
                        module.addError(inc, String.format(" is already defined in %s",
                            exist.toString()));
                    } else {
                        ret.put(inc.getValue(), inc);
                    }
                }
            }
        }
        return ret;
    }
}
