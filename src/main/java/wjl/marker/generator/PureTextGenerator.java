package wjl.marker.generator;

import wjl.marker.utils.ExecuteContext;
import wjl.marker.utils.StringFrag;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 直接复制模板中的文本。
 */
public class PureTextGenerator extends AbstractGenerator {
    private final StringFrag pureText;

    public PureTextGenerator(StringFrag pureText) {
        this.pureText = pureText;
    }

    @Override
    public void eval(ExecuteContext context, OutputStreamWriter writer) throws IOException {
        writer.write(pureText.getBuffer(), pureText.getOff(), pureText.getLen());
    }
}
