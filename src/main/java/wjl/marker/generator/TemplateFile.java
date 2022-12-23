package wjl.marker.generator;

/**
 * 一个模板文件，解析后变成一串生成器，模板文件可以包含另一个模板文件。
 */
public class TemplateFile extends ComposedGenerator {
    private final String name;
    private final char [] content;

    /**
     * 构造函数
     *
     * @param name 模板文件的名称，用于定位问题
     * @param content 模板文件的原始内容
     */
    public TemplateFile(String name, char [] content) {
        this.name = name;
        this.content = content;
    }
}
