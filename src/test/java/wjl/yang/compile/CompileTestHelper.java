package wjl.yang.compile;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.YangGrammarChecker;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;
import wjl.yang.utils.YangError;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CompileTestHelper {
    private static final String BASE_DIR = "src/test/resources/";
    private final List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors.isEmpty() ? null : errors;
    }

    /**
     * 解析单个模块，不支持import和include语句，大部分测试场景够用了。
     *
     * @param filename 模型文件名
     * @return 只做了基本校验的模块
     */
    public YangMainModule parseMainModule(String filename) {
        errors.clear();
        YangGrammarChecker checker = new YangGrammarChecker();
        try (InputStream fin = new FileInputStream(BASE_DIR + filename)) {
            YangLex lex = new YangLex(fin);
            YangParser parser = new YangParser();
            YangStmt stmt = parser.parse(lex);
            if (!checker.check(stmt)) {
                errors.add(filename + " has errors:");
                for (YangError err : checker.getErrors()) {
                    errors.add("  " + err.toString());
                }
            } else {
                return mainModule(stmt);
            }
        } catch (IOException | YangParseException err) {
            errors.add(filename + " : " + err.getMessage());
        }
        return null;
    }

    private YangMainModule mainModule(YangStmt stmt) {
        String pre = CompileUtil.prefix(stmt);
        YangMainModule ret = new YangMainModule(stmt.getValue(), pre, CompileUtil.version(stmt));
        ret.addPrefix(pre, ret);
        ret.setStmt(stmt);
        stmt.iterateAll((sub) -> {
            sub.setOriModule(ret);
        });
        return ret;
    }
}
