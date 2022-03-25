package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangStmtClone;
import wjl.yang.model.YangSubModule;
import wjl.yang.utils.YangKeyword;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class CompileUtil {
    private static final Set<String> SCHEMA_NODE_KEYS;

    static {
        SCHEMA_NODE_KEYS = new HashSet<>();
        SCHEMA_NODE_KEYS.add(YangKeyword.CONTAINER);
        SCHEMA_NODE_KEYS.add(YangKeyword.LIST);
        SCHEMA_NODE_KEYS.add(YangKeyword.LEAF);
        SCHEMA_NODE_KEYS.add(YangKeyword.LEAF_LIST);
        SCHEMA_NODE_KEYS.add(YangKeyword.ANYDATA);
        SCHEMA_NODE_KEYS.add(YangKeyword.ANYXML);
        SCHEMA_NODE_KEYS.add(YangKeyword.CHOICE);
        SCHEMA_NODE_KEYS.add(YangKeyword.CASE);
    }

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

    /**
     * 深度复制一条语句
     *
     * @param belong
     * @param source
     * @return
     */
    static YangStmtClone cloneStmt(YangModule belong, YangStmt uses, YangStmt source) {
        YangStmtClone clone = new YangStmtClone();
        clone.setOriModule(belong);
        clone.setSource(source);
        clone.setUses(uses);
        clone.setKey(source.getKey());
        clone.setValue(source.getValue());
        clone.setValueToken(source.getValueToken());
        if (source.getSubStatements() != null) {
            for (YangStmt sub : source.getSubStatements()) {
                clone.addSubStatement(cloneStmt(belong, uses, sub));
            }
        }
        return clone;
    }

    /**
     * 搜索模型节点
     *
     * @param module 当前模板，用于根据前缀找模块
     * @param pos 出现错误时定位到此语句
     * @param top 搜索的起点
     * @param path 搜索的路径
     * @return 找到的模型节点，返回空时肯定会填错误信息
     */
    static YangStmt searchSchemaNode(YangModule module, YangStmt pos, YangStmt top, List<String> path) {
        YangStmt curr = top;
        for (String hop : path) {
            ModuleAndIdentify mi = module.separate(hop, true);
            if (mi == null) {
                module.addError(pos, hop + " invalid prefix.");
                return null;
            }

            YangStmt found = null;
            if (curr.getSubStatements() != null) {
                for (YangStmt sub : curr.getSubStatements()) {
                    if (matchModuleAndId(mi, sub)) {
                        found = sub;
                        break;
                    }
                }
            }

            curr = found;
            if (found == null) {
                module.addError(pos, hop + " is not found.");
            }
        }

        return curr;
    }

    private static boolean matchModuleAndId(ModuleAndIdentify mi, YangStmt stmt) {
        if (!Objects.equals(mi.getIdentify(), stmt.getValue())) {
            return false;
        }
        if (mi.getModule() != stmt.getOriModule() && mi.getModule() != stmt.getMainModule()) {
            return false;
        }
        return SCHEMA_NODE_KEYS.contains(stmt.getKey());
    }
}
