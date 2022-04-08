package wjl.yang.writer;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangToken;
import wjl.yang.utils.YangError;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 以YANG模型的格式输出错误信息，只输出有错误的语句，方便阅读。
 */
public class YangErrorWriter {
    private static final Set<String> NEED_PREFIX_KEYS;
    private YangMainModule schemaModule;

    private final Map<YangStmt, String> stmtToErr = new HashMap<>();
    private YangModule currModule;
    private boolean externalErrorMode;

    static {
        // 需要加前缀标识的语句
        NEED_PREFIX_KEYS = new HashSet<>();
        NEED_PREFIX_KEYS.add(YangKeyword.CONTAINER);
        NEED_PREFIX_KEYS.add(YangKeyword.LIST);
        NEED_PREFIX_KEYS.add(YangKeyword.LEAF);
        NEED_PREFIX_KEYS.add(YangKeyword.LEAF_LIST);
        NEED_PREFIX_KEYS.add(YangKeyword.ANYDATA);
        NEED_PREFIX_KEYS.add(YangKeyword.ANYXML);
        NEED_PREFIX_KEYS.add(YangKeyword.CHOICE);
        NEED_PREFIX_KEYS.add(YangKeyword.CASE);
    }


    public void write(OutputStreamWriter out, YangModule module) throws IOException {
        if (module == null || module.getStmt() == null || out == null) {
            return;
        }

        schemaModule = module.getMainModule();
        currModule = module;
        buildErrorIndex(module);
        writeModule(out, module);
        out.flush();
        stmtToErr.clear();
    }

    private void buildErrorIndex(YangModule module) {
        List<YangError> errorList = module.getErrors();
        if (errorList == null || errorList.isEmpty()) {
            return;
        }

        for (YangError err : errorList) {
            stmtToErr.put(err.getPos(), err.getMsg());
        }

        buildErrorIndex(module.getStmt());
    }

    private boolean buildErrorIndex(YangStmt stmt) {
        boolean hasErr = false;
        if (stmt.getSubStatements() != null) {
            for (YangStmt sub : stmt.getSubStatements()) {
                if (buildErrorIndex(sub)) {
                    hasErr = true;
                }
            }
        }

        if (stmtToErr.containsKey(stmt)) {
            return true;
        }
        if (hasErr) {
            stmtToErr.putIfAbsent(stmt, "");
        }
        return hasErr;
    }

    private void writeModule(OutputStreamWriter out, YangModule module) throws IOException {
        List<YangError> errorList = module.getErrors();
        if (errorList == null || errorList.isEmpty()) {
            return;
        }

        YangStmt moduleStmt = module.getStmt();
        writeKeyValue(out, moduleStmt);
        out.write(" {\n");
        if (moduleStmt.getSubStatements() != null) {
            for (YangStmt sub : moduleStmt.getSubStatements()) {
                writeStmt(out, sub, 1);
            }
        }

        externalErrorMode = true;
        for (YangError err : errorList) {
            if (stmtToErr.containsKey(err.getPos())) {
                writeStmt(out, err.getPos(), 1);
            }
        }

        out.write("}\n");
    }

    private void writeStmt(OutputStreamWriter out, YangStmt stmt, int indent) throws IOException {
        String err = stmtToErr.remove(stmt);
        if (err == null) {
            return;
        }

        writeIndent(out, indent);
        writeKeyValue(out, stmt);
        out.write(" {\n");
        if (stmt.getSubStatements() != null) {
            for (YangStmt sub : stmt.getSubStatements()) {
                writeStmt(out, sub, indent + 1);
            }
        }

        if (!err.isEmpty()) {
            writeIndent(out, indent + 1);
            out.write("error \"");
            out.write(err);
            out.write("\";\n");
        }

        writeIndent(out, indent);
        out.write("}\n");
    }

    private void writeKeyValue(OutputStreamWriter out, YangStmt stmt) throws IOException {
        out.write(stmt.getKey());
        out.write(' ');
        writePrefix(out, stmt);
        if (stmt.getValueToken() == YangToken.STRING) {
            out.write('"');
            out.write(stmt.getValue());
            out.write('"');
        } else if (stmt.getValue() != null) {
            out.write(stmt.getValue());
        }
    }

    private void writePrefix(OutputStreamWriter out, YangStmt stmt) throws IOException {
        if (externalErrorMode) {
            if (stmt.getOriModule() != currModule) {
                out.write(stmt.getOriModule().getName());
                out.write(':');
            }
        } else {
            if (stmt.getSchemaModule() != schemaModule && NEED_PREFIX_KEYS.contains(stmt.getKey())) {
                out.write(stmt.getSchemaModule().getPrefix());
                out.write(':');
            }
        }
    }

    private void writeIndent(OutputStreamWriter out, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            out.write("  ");
        }
    }
}
