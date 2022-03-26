package wjl.yang.writer;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.compile.CompileTestHelper;
import wjl.yang.model.YangMainModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class YangWriterTest {
    @Test
    public void testYangWrite() throws IOException {
        CompileTestHelper parser = new CompileTestHelper();
        YangMainModule module = parser.parseMainModule("complex/grouping.yang");
        Assert.assertNull(parser.getErrors());

        ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
        OutputStreamWriter out = new OutputStreamWriter(bos);
        YangWriter writer = new YangWriter();
        writer.write(out, module);
        //System.out.println(bos.toString());
    }
}