package wjl.marker.generator;

import wjl.marker.utils.ExecuteContext;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 将多个生成器合并在一起。
 */
public class ComposedGenerator extends AbstractGenerator {
    private final List<AbstractGenerator> items = new ArrayList<>();

    /**
     * 添加一个成员
     *
     * @param item 子生成器
     */
    public void append(AbstractGenerator item) {
        items.add(item);
    }

    @Override
    public void eval(ExecuteContext context, OutputStreamWriter writer) throws IOException {
        for (AbstractGenerator item : items) {
            item.eval(context, writer);
        }
    }
}
