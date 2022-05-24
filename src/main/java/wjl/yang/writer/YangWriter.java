package wjl.yang.writer;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangToken;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * 输出为YANG模型文件。
 */
public class YangWriter {
    private static final Set<String> NEED_PREFIX_KEYS;
    private final boolean reduce;
    private YangMainModule schemaModule;

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

    public YangWriter(boolean reduce) {
        this.reduce = reduce;
    }

    public void write(OutputStreamWriter out, YangModule module) throws IOException {
        if (module == null || module.getStmt() == null || out == null) {
            return;
        }

        schemaModule = module.getMainModule();
        writeStmt(out, module.getStmt(), 0);
        out.flush();
    }

    public void write(OutputStreamWriter out, YangStmt stmt) throws IOException {
        if (stmt == null || out == null) {
            return;
        }
        schemaModule = stmt.getSchemaModule();
        writeStmt(out, stmt, 0);
        out.flush();
    }

    private void writeStmt(OutputStreamWriter out, YangStmt stmt, int indent) throws IOException {
        writeIndent(out, indent);
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

        if (stmt.getSubStatements() == null || ignoreSub(stmt)) {
            out.write(";\n");
        } else {
            out.write(" {\n");
            for (YangStmt sub : stmt.getSubStatements()) {
                writeStmt(out, sub, indent + 1);
            }
            writeIndent(out, indent);
            out.write("}\n");
        }
    }

    private boolean ignoreSub(YangStmt stmt) {
        return reduce && YangKeyword.AUGMENT.equals(stmt.getKey());
    }

    private void writePrefix(OutputStreamWriter out, YangStmt stmt) throws IOException {
        if (stmt.getSchemaModule() != schemaModule && NEED_PREFIX_KEYS.contains(stmt.getKey())) {
            out.write(stmt.getSchemaModule().getByPrefix());
            out.write(':');
        }
    }

    private void writeIndent(OutputStreamWriter out, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            out.write("  ");
        }
    }
}
