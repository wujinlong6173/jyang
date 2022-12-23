package wjl.marker.generator;

import wjl.marker.utils.ExecuteContext;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 文本生成器的顶层类。
 */
public abstract class AbstractGenerator {
    /**
     * 执行并输出文本
     *
     * @param context 执行时的上下文
     * @param writer 接收执行输出的文本
     * @throws IOException 文本接收器的异常
     */
    public abstract void eval(ExecuteContext context, OutputStreamWriter writer) throws IOException;
}
