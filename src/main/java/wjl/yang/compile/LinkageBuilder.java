package wjl.yang.compile;

import wjl.yang.model.YangContext;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;
import wjl.yang.utils.YangKeyword;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 处理import和include语句，填写YangModule.prefixToModule和subModules。
 */
class LinkageBuilder {
    static void build(YangContext context, List<YangMainModule> modules) {
        includeInMain(context, modules);
        includeInSub(context, modules);
        imports(context, modules);
        for (YangModule module : modules) {
            imports(context, module.getSubModules());
        }
    }

    private static void includeInMain(YangContext context, List<YangMainModule> modules) {
        for (YangMainModule module : modules) {
            YangStmt stmt = module.getStmt();
            Set<String> included = new HashSet<>();
            stmt.forEach(YangKeyword.INCLUDE, (sub) -> {
                String name = sub.getValue();
                String ver = CompileUtil.revisionDate(sub);
                YangSubModule target = context.matchSubModule(name, ver);
                if (target == null) {
                    module.addError(sub, "no matched submodule.");
                } else if (!Objects.equals(module.getName(), target.getBelongTo())) {
                    module.addError(sub, "submodule not belongs to this module.");
                } else if (included.contains(name)) {
                    module.addError(sub, "can not include two submodule with same name.");
                } else {
                    included.add(name);
                    target.setMainModule(module);
                    target.addPrefix(target.getByPrefix(), module);
                    module.addSubModule(target);
                }
            });
        }
    }

    private static void includeInSub(YangContext context, List<YangMainModule> modules) {
        for (YangMainModule main : modules) {
            List<YangSubModule> subModules = main.getSubModules();
            for (YangSubModule module : subModules) {
                YangStmt stmt = module.getStmt();
                Set<String> included = new HashSet<>();
                stmt.forEach(YangKeyword.INCLUDE, (sub) -> {
                    String name = sub.getValue();
                    String ver = CompileUtil.revisionDate(sub);
                    YangSubModule target = context.matchSubModule(name, ver);
                    if (target == null) {
                        module.addError(sub, "no matched submodule.");
                    } else if (!Objects.equals(module.getBelongTo(), target.getBelongTo())) {
                        module.addError(sub, "submodule not belongs to same module.");
                    } else if (included.contains(name)) {
                        module.addError(sub, "can not include two submodule with same name.");
                    } else if (target.getMainModule() == null) {
                        module.addError(sub, "submodule should be included by main module.");
                    } else {
                        included.add(name);
                        module.addSubModule(target);
                    }
                });
            }
        }
    }

    // 处理import子句
    private static void imports(YangContext context, List<? extends YangModule> modules) {
        for (YangModule module : modules) {
            YangStmt stmt = module.getStmt();

            stmt.forEach(YangKeyword.IMPORT, (sub) -> {
                String name = sub.getValue();
                if (!Objects.equals(name, stmt.getValue())) {
                    String pre = CompileUtil.prefix(sub);
                    YangModule exist = module.getByPrefix(pre);
                    if (exist != null) {
                        module.addError(sub,  "prefix conflict with " + exist.getName() + '.');
                    } else {
                        String ver = CompileUtil.revisionDate(sub);
                        YangMainModule target = context.matchMainModule(name, ver);
                        if (target == null) {
                            module.addError(sub, "no matched module.");
                        } else {
                            module.addPrefix(pre, target);
                        }
                    }
                }
            });
        }
    }
}
