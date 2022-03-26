package wjl.yang.writer;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangToken;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 输出为YANG模型文件。
 */
public class YangWriter {
    public void write(OutputStreamWriter out, YangModule module) throws IOException {
        if (module == null || module.getStmt() == null || out == null) {
            return;
        }

        writeStmt(out, module.getStmt(), 0);
        out.flush();
    }

    private void writeStmt(OutputStreamWriter out, YangStmt stmt, int indent) throws IOException {
        writeIndent(out, indent);
        out.write(stmt.getKey());
        out.write(' ');
        if (stmt.getValueToken() == YangToken.STRING) {
            out.write('"');
            out.write(stmt.getValue());
            out.write('"');
        } else {
            out.write(stmt.getValue());
        }

        if (stmt.getSubStatements() == null) {
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

    private void writeIndent(OutputStreamWriter out, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            out.write("  ");
        }
    }
}
