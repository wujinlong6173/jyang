package wjl.yang.compile;

import wjl.yang.model.ModuleNameVersion;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangSubModule;
import wjl.yang.parser.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.Objects;

public class YangModuleCompiler {
    public YangModule module(YangStmt stmt) {
        if (Objects.equals(YangKeyword.MODULE, stmt.getKey())) {
            return mainModule(stmt);
        } else {
            return subModule(stmt);
        }
    }

    public YangMainModule mainModule(YangStmt stmt) {
        String pre = prefix(stmt);
        YangMainModule ret = new YangMainModule(stmt.getValue(), pre, version(stmt));
        ret.addPrefix(pre, new ModuleNameVersion(stmt.getValue(), null));
        imports(stmt, ret);
        return ret;
    }

    public YangSubModule subModule(YangStmt stmt) {
        String parent = null;
        String pre = null;
        YangStmt belongsTo = stmt.searchOne(YangKeyword.BELONGS_TO);
        if (belongsTo != null) {
            parent = belongsTo.getValue();
            pre = prefix(belongsTo);
        }

        YangSubModule ret = new YangSubModule(stmt.getValue(), version(stmt), pre, parent);
        ret.addPrefix(pre, new ModuleNameVersion(stmt.getValue(), null));
        imports(stmt, ret);
        return ret;
    }

    private String version(YangStmt stmt) {
        YangStmt version = stmt.searchOne(YangKeyword.REVISION);
        return version != null ? version.getValue() : null;
    }

    private String prefix(YangStmt stmt) {
        YangStmt prefix = stmt.searchOne(YangKeyword.PREFIX);
        return prefix != null ? prefix.getValue() : null;
    }

    private String revisionDate(YangStmt stmt) {
        YangStmt ver = stmt.searchOne(YangKeyword.REVISION_DATE);
        return ver != null ? ver.getValue() : null;
    }

    private void imports(YangStmt stmt, YangModule module) {
        stmt.forEach(YangKeyword.IMPORT, (sub) -> {
            String name = sub.getValue();
            if (!Objects.equals(name, stmt.getValue())) {
                String pre = prefix(sub);
                if (module.getPrefix(pre) != null) {
                    module.addError(sub, "prefix conflict.");
                } else {
                    String ver = revisionDate(sub);
                    module.addPrefix(pre, new ModuleNameVersion(name, ver));
                }
            }
        });
    }
}
