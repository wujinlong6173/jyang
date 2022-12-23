package wjl.marker.generator;

import wjl.marker.expression.AbstractExpression;
import wjl.marker.utils.ExecuteContext;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;

/**
 * 条件控制
 */
public class ConditionGenerator extends AbstractGenerator {
    private final AbstractExpression condition;
    private final AbstractGenerator succeed;
    private final AbstractGenerator failed;

    /**
     * 构造函数
     *
     * @param condition 判断条件，必填
     * @param succeed 条件成立时使用的生成器，必填
     * @param failed 条件不成立时使用的生成器，可选
     */
    public ConditionGenerator(AbstractExpression condition,
        AbstractGenerator succeed, AbstractGenerator failed) {
        this.condition = condition;
        this.succeed = succeed;
        this.failed = failed;
    }

    @Override
    public void eval(ExecuteContext context, OutputStreamWriter writer) throws IOException {
        Object cond = condition.eval(context);
        if (isSucceed(cond, context)) {
            succeed.eval(context, writer);
        } else if (failed != null) {
            failed.eval(context, writer);
        }
    }

    private boolean isSucceed(Object data, ExecuteContext context) {
        if (data == null) {
            return false;
        } else if (data instanceof Boolean) {
            return Boolean.TRUE.equals(data);
        } else if (data instanceof Number) {
            return ((Number)data).intValue() != 0;
        } else if (data instanceof Collection) {
            return !((Collection<?>)data).isEmpty();
        } else if (data instanceof Map) {
            return !((Map<?,?>)data).isEmpty();
        } else {
            // 警告：不认识的数据类型被作为条件
            context.warning(condition.getLocation(), "condition must be Boolean Number Collection or Map.");
            return true;
        }
    }
}
