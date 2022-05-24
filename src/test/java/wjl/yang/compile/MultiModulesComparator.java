package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;
import wjl.yang.utils.YangError;
import wjl.yang.writer.YangErrorWriter;
import wjl.yang.writer.YangWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 测试辅助工具，允许在一个文件中定义多个模块，处理后和预期的结果做比较。
 */
public class MultiModulesComparator {

    private final String inputFile;
    private final String outputFile;
    private final String errorFile;
    private final YangModuleCompiler compiler = new YangModuleCompiler();
    private String resultStr;
    private String outputStr;
    private String inputErr;
    private String expectErr;

    public MultiModulesComparator(String baseDir, String caseName) {
        inputFile = String.format("%s/%s/%s_input.yang", baseDir, caseName, caseName);
        outputFile = String.format("%s/%s/%s_output.yang", baseDir, caseName, caseName);
        errorFile = String.format("%s/%s/%s_error.yang", baseDir, caseName, caseName);
    }

    public void compare() {
        List<YangStmt> inputs = readFile(inputFile);
        List<YangStmt> outputs = readFile(outputFile);
        List<YangStmt> errors = readFile(errorFile);
        if (compiler.hasError() || inputs == null) {
            return;
        }

        List<YangModule> inputModules = compiler.compileStmtList(inputs);
        copyErrorInModules(inputModules);
        if (outputs != null) {
            resultStr = moduleToString(inputModules);
            outputStr = stmtToString(outputs, true);
        }
        if (errors != null) {
            inputErr = errToString(inputModules);
            expectErr = stmtToString(errors, false);
        }
    }

    public String getResultStr() {
        return resultStr;
    }

    public String getOutputStr() {
        return outputStr;
    }

    public String getInputErr() {
        return inputErr;
    }

    public String getExpectErr() {
        return expectErr;
    }

    public List<String> getErrors() {
        return compiler.getErrors();
    }

    private List<YangStmt> readFile(String filename) {
        File file = new File(filename);
        if (!file.isFile()) {
            return null;
        }

        try (InputStream fin = new FileInputStream(filename)) {
            YangLex lex = new YangLex(fin);
            YangParser parser = new YangParser();
            return parser.parseFragment(lex);
        } catch (IOException | YangParseException err) {
            compiler.addError(filename, err.getMessage());
            return null;
        }
    }

    private void copyErrorInModules(List<YangModule> modules) {
        for (YangModule module :modules) {
            if (!module.getErrors().isEmpty()) {
                compiler.addError(module.getName() + " have errors:");
                for (YangError err : module.getErrors()) {
                    compiler.addError(err.toString());
                }
            }
        }
    }

    private String moduleToString(List<YangModule> modules) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        OutputStreamWriter out = new OutputStreamWriter(bos);
        YangWriter writer = new YangWriter(true);
        try {
            for (YangModule module : modules) {
                writer.write(out, module);
            }
            return bos.toString();
        } catch (IOException err) {
            compiler.addError("write model failed : " + err.getMessage());
            return null;
        }
    }

    private String stmtToString(List<YangStmt> stmtList, boolean reduce) {
        if (stmtList == null) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        OutputStreamWriter out = new OutputStreamWriter(bos);
        YangWriter writer = new YangWriter(reduce);
        try {
            for (YangStmt stmt : stmtList) {
                writer.write(out, stmt);
            }
            return bos.toString();
        } catch (IOException err) {
            compiler.addError("write model failed : " + err.getMessage());
            return null;
        }
    }

    private boolean compareStmtList(List<YangStmt> results, List<YangStmt> outputs) {
        if (results == null && outputs == null) {
            return true;
        } else if (results == null || outputs == null) {
            return false;
        } else if (results.size() != outputs.size()) {
            return false;
        }

        Iterator<YangStmt> it1 = results.iterator();
        Iterator<YangStmt> it2 = results.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            YangStmt res = it1.next();
            YangStmt out = it1.next();
            if (!compareStmt(res, out)) {
                return false;
            }
        }

        return true;
    }

    private boolean compareStmt(YangStmt result, YangStmt output) {
        if (result == null && output == null) {
            return true;
        } else if (result == null || output == null) {
            return false;
        }

        return Objects.equals(result.getKey(), output.getKey())
            && Objects.equals(result.getValue(), output.getValue())
            && compareStmtList(result.getSubStatements(), output.getSubStatements());
    }

    private String errToString(List<YangModule> modules) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        OutputStreamWriter out = new OutputStreamWriter(bos);
        YangErrorWriter errorWriter = new YangErrorWriter();
        try {
            for (YangModule module : modules) {
                errorWriter.write(out, module);
            }
            return bos.toString();
        } catch (IOException err) {
            compiler.addError("write model failed : " + err.getMessage());
            return null;
        }
    }
}
