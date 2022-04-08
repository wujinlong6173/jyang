package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 有些定义语句和使用语句是配对的，处理规则是相似的。
 */
abstract class DefineAndUseCompiler {
    private final String defKey;
    private final String useKey;
    private final Map<YangModule, Map<String, YangStmt>> moduleToDefines;

    /**
     *
     * @param defKey 定义语句的键值
     * @param useKey 使用语句的键值
     */
    DefineAndUseCompiler(String defKey, String useKey) {
        this.defKey = defKey;
        this.useKey = useKey;
        this.moduleToDefines = new HashMap<>();
    }

    /**
     * 收集模块、子模块中定义的特定语句，并复制子模块中的语句，根据名称检查重复定义。
     *
     * @param modules 主模块和子模块
     */
    void searchDefineInModules(List<YangModule> modules) {
        // 搜索每个模块、子模块中的指定语句。
        Map<YangModule, Map<String, YangStmt>> moduleToStmt = new HashMap<>();
        for (YangModule module : modules) {
            moduleToStmt.put(module, searchDefineInModule(module));
        }

        // 复制子模块定义的预置组
        for (YangModule module : modules) {
            moduleToDefines.put(module, copyIncludedStmt(module, moduleToStmt));
        }
    }

    private Map<String, YangStmt> searchDefineInModule(YangModule module) {
        Map<String, YangStmt> stmtMap = new HashMap<>();
        module.getStmt().forEach(defKey, (group) -> {
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

    private Map<String, YangStmt> copyIncludedStmt(YangModule module,
        Map<YangModule, Map<String, YangStmt>> moduleToStmt) {
        Map<String, YangStmt> ret = new HashMap<>();

        Map<String, YangStmt> temp;
        for (YangSubModule sub : module.getSubModules()) {
            temp = moduleToStmt.get(sub);
            mergeDefines(module, ret, temp);
        }

        temp = moduleToStmt.get(module);
        mergeDefines(module, ret, temp);
        return ret;
    }

    private void mergeDefines(YangModule module, Map<String, YangStmt> ret, Map<String, YangStmt> temp) {
        if (temp != null) {
            for (YangStmt inc : temp.values()) {
                YangStmt exist = ret.get(inc.getValue());
                if (exist != null) {
                    module.addError(inc, String.format("conflict with %s.",
                        exist.toString()));
                } else {
                    ret.put(inc.getValue(), inc);
                }
            }
        }
    }

    /**
     * 搜索所有使用语句。
     *
     * @param modules 模块和子模块
     * @param hasLocalDefine 是否有出现在语句内部的定义
     */
    void searchUseInModules(List<YangModule> modules, boolean hasLocalDefine) {
        for (YangModule module : modules) {
            YangStmt stmtModule = module.getStmt();
            if (stmtModule.getSubStatements() == null) {
                continue;
            }

            Map<String, YangStmt> moduleDefines = moduleToDefines.get(module);
            if (hasLocalDefine) {
                Map<String, YangStmt> scopeDefines = moduleDefines == null
                    ? new HashMap<>() : new HashMap<>(moduleDefines);
                for (YangStmt topStmt : stmtModule.getSubStatements()) {
                    searchUseAndLocalDefine(null, stmtModule, topStmt, scopeDefines);
                }
            } else {
                for (YangStmt topStmt : stmtModule.getSubStatements()) {
                    searchUseOnly(null, stmtModule, topStmt, moduleDefines);
                }
            }
        }
    }

    private void searchUseAndLocalDefine(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeDefines) {
        if (useKey.equals(stmt.getKey())) {
            onUse(parentDefine, parentStmt, stmt, scopeDefines);
        } else if (defKey.equals(stmt.getKey())) {
            parentDefine = stmt;
        } else if (checkHiddenDefine(parentDefine, parentStmt, stmt)) {
            parentDefine = stmt;
        }

        if (stmt.getSubStatements() == null) {
            return;
        }

        // 先收集本语句中的定义，再递归处理子语句
        Set<String> localDefines = null;
        for (YangStmt define : stmt.getSubStatements()) {
            if (defKey.equals(define.getKey())) {
                String name = define.getValue();
                YangStmt exist = scopeDefines.get(name);
                if (exist != null) {
                    stmt.getOriModule().addError(define, String.format("conflict with %s.", exist.toString()));
                } else {
                    scopeDefines.put(name, define);
                    if (localDefines == null) {
                        localDefines = new HashSet<>();
                    }
                    localDefines.add(name);
                }
            }
        }

        for (YangStmt sub : stmt.getSubStatements()) {
            searchUseAndLocalDefine(parentDefine, stmt, sub, scopeDefines);
        }

        if (localDefines != null) {
            for (String local : localDefines) {
                scopeDefines.remove(local);
            }
        }
    }

    private void searchUseOnly(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> moduleDefines) {
        if (useKey.equals(stmt.getKey())) {
            onUse(parentDefine, parentStmt, stmt, moduleDefines);
        } else if (defKey.equals(stmt.getKey())) {
            parentDefine = stmt;
        }

        if (stmt.getSubStatements() == null) {
            return;
        }

        for (YangStmt sub : stmt.getSubStatements()) {
            searchUseOnly(parentDefine, stmt, sub, moduleDefines);
        }
    }

    /**
     * 搜索到使用语句时调用本方法。
     *
     * @param parentDefine 父定义
     * @param parentStmt 父语句
     * @param stmt 使用语句
     * @param scopeDefines 可见的定义
     */
    protected abstract void onUse(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeDefines);

    /**
     * 判断是不是匿名的定义，如果是匿名的定义，应该在这个函数内完成处理。
     *
     * @param parentDefine 父定义
     * @param parentStmt 父语句
     * @param stmt 要判断的语句
     * @return 是不是匿名的定义
     */
    protected boolean checkHiddenDefine(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt) {
        return false;
    }

    /**
     * 查找定义，没找到时会报错。
     *
     * @param use 使用语句
     * @param scopeDefines 可见的定义
     * @return 找到的定义或空
     */
    protected YangStmt findTargetDefine(YangStmt use, Map<String, YangStmt> scopeDefines) {
        YangModule oriModule = use.getOriModule();
        YangStmt target = null;
        ModuleAndIdentify mi = oriModule.separate(use.getValue(), false);
        if (mi == null) {
            oriModule.addError(use, "invalid prefix.");
            return null;
        } else if (mi.getModule() == null) {
            target = scopeDefines.get(mi.getIdentify());
        } else {
            Map<String, YangStmt> moduleDefines = moduleToDefines.get(mi.getModule());
            if (moduleDefines != null) {
                target = moduleDefines.get(mi.getIdentify());
            }
        }

        if (target == null) {
            oriModule.addError(use,  "undefined.");
        }
        return target;
    }

    /**
     * 查找定义，不会报错。
     *
     * @param mi
     * @return
     */
    protected YangStmt getDefine(ModuleAndIdentify mi) {
        Map<String, YangStmt> defines = moduleToDefines.get(mi.getModule());
        if (defines == null) {
            return null;
        }
        return defines.get(mi.getIdentify());
    }
}
