package wjl.yang.compile;

import org.junit.Test;
import wjl.yang.model.YangModule;
import wjl.yang.parser.*;
import wjl.yang.utils.YangError;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 测试模块和子模块，包括import和include语句。
 */
public class YangModuleTest {
    private String dir = "src/test/resources/vpn/";
    private String[] files = new String[]{
            "l2vpn.yang", "l2vpn-bfd.yang", "l2vpn-oam.yang", "l2vpn-vll.yang",
            "l3vpn.yang", "l3vpn-bgp.yang", "network.yang"};

    @Test
    public void test() {
        YangGrammarChecker checker = new YangGrammarChecker();
        YangModuleCompiler compiler = new YangModuleCompiler();

        for (String filename : files) {
            String fullName = dir + filename;
            try (InputStream fin = new FileInputStream(fullName)) {
                YangLex lex = new YangLex(fin);
                YangParser parser = new YangParser();
                YangStmt stmt = parser.parse(lex);
                if (!checker.check(stmt)) {
                    printErrors(filename, checker.getErrors());
                } else {
                    YangModule module = compiler.module(stmt);
                    System.out.println(module);
                }
            } catch (IOException | YangParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void printErrors(String filename, List<YangError> errors) {
        System.err.println("error at file " + filename);
        for (YangError err : errors) {
            System.err.println(err.toString());
        }
    }
}
