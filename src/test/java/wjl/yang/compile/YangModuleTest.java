package wjl.yang.compile;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试模块和子模块，包括import和include语句。
 */
public class YangModuleTest {
    private String dir = "src/test/resources/vpn/";
    private String[] files = new String[]{
            "l2vpn.yang", "l2vpn-bfd.yang", "l2vpn-oam.yang", "l2vpn-vll.yang",
            "l3vpn.yang", "l3vpn-bgp.yang", "network.yang", "common.yang"};

    @Test
    public void test() {
        YangModuleCompiler compiler = new YangModuleCompiler();
        List<String> filenames = new ArrayList<>(files.length);
        for (String filename : files) {
            filenames.add(dir + filename);
        }

        compiler.compile(filenames);
        for (String err : compiler.getErrors()) {
            System.err.println(err);
        }
    }
}
