package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class DefineAndUseCompiler {
    private final String defKey;
    private final String useKey;
    private Map<YangModule, Map<String, YangStmt>> moduleToDefines;

    DefineAndUseCompiler(String defKey, String useKey) {
        this.defKey = defKey;
        this.useKey = useKey;
    }

    /**
     *
     * @param modules 主模块和子模块。
     */
    void match(List<YangModule> modules) {
        // 先搜索模块顶层定义的typedef
        moduleToDefines = CompileUtil.collectSpecialStmt(modules, defKey);
        // 再搜索语句内定义的 typedef 和 type 语句
        searchDefineAndUse(modules);
    }

    private void searchDefineAndUse(List<YangModule> modules) {
        for (YangModule module : modules) {
            YangStmt stmtModule = module.getStmt();
            if (stmtModule.getSubStatements() == null) {
                continue;
            }

            Map<String, YangStmt> moduleDefines = moduleToDefines.get(module);
            Map<String, YangStmt> scopeDefines = moduleToDefines == null
                ? new HashMap<>() : new HashMap<>(moduleDefines);

            for (YangStmt topStmt : stmtModule.getSubStatements()) {
                searchDefineAndUse(null, stmtModule, topStmt, scopeDefines);
            }
        }
    }

    private void searchDefineAndUse(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeDefines) {
        if (useKey.equals(stmt.getKey())) {
            YangStmt targetDefine = findTargetDefine(stmt, scopeDefines);
            if (targetDefine != null) {
                onMatch(parentDefine, parentStmt, stmt, targetDefine);
            }
        } else if (defKey.equals(stmt.getKey())) {
            parentDefine = stmt;
        } else if (checkHiddenDefine(parentStmt, stmt)) {
            onMatch(parentDefine, null, parentStmt, stmt);
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
                    stmt.getOriModule().addError(define, String.format(" is already defined in %s", exist.toString()));
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
            searchDefineAndUse(parentDefine, stmt, sub, scopeDefines);
        }

        if (localDefines != null) {
            for (String local : localDefines) {
                scopeDefines.remove(local);
            }
        }
    }

    private YangStmt findTargetDefine(YangStmt use, Map<String, YangStmt> scopeDefines) {
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
            oriModule.addError(use,  String.format("undefined %s.", defKey));
        }
        return target;
    }

    /**
     * 判断是不是匿名的定义
     *
     * @param parentStmt 父语句
     * @param stmt 被判断的语句
     * @return 是不是匿名的定义
     */
    protected boolean checkHiddenDefine(YangStmt parentStmt, YangStmt stmt) {
        return false;
    }

    /**
     * 找到使用定义的地方
     *
     * @param parentDefine 父定义
     * @param parentStmt 父语句
     * @param use 使用定义的语句
     * @param targetDefine 被使用的定义
     */
    protected abstract void onMatch(YangStmt parentDefine, YangStmt parentStmt, YangStmt use, YangStmt targetDefine);
}
