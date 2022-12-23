package wjl.marker.generator;

import wjl.marker.expression.AbstractExpression;
import wjl.marker.utils.ExecuteContext;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 将表达式输出的数据转换成文本并输出。
 */
public class ExpressionGenerator extends AbstractGenerator {
    private final AbstractExpression expression;

    @Override
    public void eval(ExecuteContext context, OutputStreamWriter writer) throws IOException {
        Object ret = expression.eval(context);
        if (ret != null) {
            writer.write(ret.toString());
        }
    }

    /**
     * 构造函数
     *
     * @param expression 表达式
     */
    public ExpressionGenerator(AbstractExpression expression) {
        this.expression = expression;
    }
}
