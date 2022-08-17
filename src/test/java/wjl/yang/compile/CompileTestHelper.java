package wjl.yang.compile;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;

public class CompileTestHelper extends YangModuleCompiler {
    private static final String BASE_DIR = "src/test/resources/";

    /**
     * 解析单个模块，不支持import和include语句，大部分测试场景够用了。
     *
     * @param filename 模型文件名
     * @return 只做了基本校验的模块
     */
    public YangMainModule parseMainModule(String filename) {
        try (InputStream fin = new FileInputStream(BASE_DIR + filename)) {
            YangLex lex = new YangLex(fin);
            YangParser parser = new YangParser();
            YangStmt stmt = parser.parse(lex);
            return (YangMainModule)addModule(stmt);
        } catch (IOException | YangParseException err) {
            Assert.fail(err.getMessage());
        }
        return null;
    }

}
