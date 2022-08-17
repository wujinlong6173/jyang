package wjl.yang.api;

import wjl.yang.compile.YangModuleCompiler;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangContextBuilder extends YangModuleCompiler {
    private static final String MUST_DEFINE_MODULE = "must define module or submodule.";
    private final List<String> parseErrors = new ArrayList<>();

    /**
     * 增加一个模型文件，只检查词法、语法错误，不检查语义错误。
     *
     * @param file 模型文件
     */
    public void addFile(File file) {
        try (InputStream fin = new FileInputStream(file)) {
            YangLex lex = new YangLex(fin);
            YangParser parser = new YangParser();
            YangStmt stmt = parser.parse(lex);
            YangModule module = addModule(stmt);
            if (module == null) {
                addError(file.getName(), MUST_DEFINE_MODULE);
            }
        } catch (IOException | YangParseException err) {
            addError(file.getName(), err.getMessage());
        }
    }

    public List<String> getParseErrors() {
        return parseErrors;
    }

    private void addError(String loc, String msg) {
        parseErrors.add(loc + " : " + msg);
    }
}
