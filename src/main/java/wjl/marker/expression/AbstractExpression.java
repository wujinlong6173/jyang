package wjl.marker.expression;

import wjl.marker.utils.ExecuteContext;
import wjl.marker.utils.StringFrag;

/**
 * 表达式的顶层类，占位符，数学表达式，逻辑表达式，函数调用。
 * 表达式输出的是简单或结构化数据，不局限于文本。
 */
public abstract class AbstractExpression {
    private StringFrag location;

    public void setLocation(StringFrag location) {
        this.location = location;
    }

    public StringFrag getLocation() {
        return location;
    }

    /**
     * 求表达式的值
     *
     * @param context 执行脚本的上下文，收集错误信息
     * @return 表达式的计算结果
     */
    public abstract Object eval(ExecuteContext context);
}
