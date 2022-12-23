package wjl.marker.generator;

import wjl.marker.expression.AbstractExpression;
import wjl.marker.utils.ExecuteContext;
import wjl.marker.utils.StringFrag;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;

/**
 * 循环控制
 */
public class LoopGenerator extends AbstractGenerator {
    private final StringFrag iterator;
    private final AbstractExpression expression;
    private final AbstractGenerator body;
    private StringFrag header;
    private StringFrag separator;
    private StringFrag footer;

    /**
     * 构造函数
     *
     * @param iterator 迭代子的名称
     * @param expression 需要迭代的数据
     * @param body 循环内的模板
     */
    public LoopGenerator(StringFrag iterator, AbstractExpression expression, AbstractGenerator body) {
        this.iterator = iterator;
        this.expression = expression;
        this.body = body;
    }

    public void setHeader(StringFrag header) {
        this.header = header;
    }

    public void setSeparator(StringFrag separator) {
        this.separator = separator;
    }

    public void setFooter(StringFrag footer) {
        this.footer = footer;
    }

    @Override
    public void eval(ExecuteContext context, OutputStreamWriter writer) throws IOException {
        Object data = expression.eval(context);
        if (data instanceof Collection) {
            loop((Collection<?>)data, context, writer);
        } else if (data instanceof Map) {
            Map<?,?> map = (Map<?,?>)data;
            loop(map.entrySet(), context, writer);
        } else if (data != null) {
            // 警告：不认识的数据类型被作为循环数据
            context.warning(expression.getLocation(), "data for loop must be Collection or Map.");
        }
    }

    private void loop(Collection<?> lst, ExecuteContext context, OutputStreamWriter writer) throws IOException {
        if (header != null) {
            writer.write(header.getBuffer(), header.getOff(), header.getLen());
        }
        boolean notFirst = false;
        for (Object item : lst) {
            if (notFirst && separator != null) {
                writer.write(separator.getBuffer(), separator.getOff(), separator.getLen());
            }
            notFirst = true;
            context.pushIterator(iterator, item);
            body.eval(context, writer);
            context.popIterator(iterator);
        }
        if (footer != null) {
            writer.write(footer.getBuffer(), footer.getOff(), footer.getLen());
        }
    }
}
