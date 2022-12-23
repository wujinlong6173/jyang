package wjl.marker.utils;

/**
 * 引用一片字符中的一个片段，解析模板时减少对象复制。
 */
public class StringFrag {
    private final char [] buffer;
    private final int off;
    private final int len;

    /**
     * 构造字符串片段
     *
     * @param buffer 引用的一片字符
     * @param off 片段的起点
     * @param len 片段的长度
     */
    public StringFrag(char [] buffer, int off, int len) {
        this.buffer = buffer;
        this.off = off;
        this.len = len;
    }

    public char [] getBuffer() {
        return buffer;
    }

    public int getOff() {
        return off;
    }

    public int getLen() {
        return len;
    }
}
