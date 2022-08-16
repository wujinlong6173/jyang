package wjl.yang.model;

/**
 * 对应Yang文件中的每一条扩展的语句。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangStmtExt extends YangStmt {
    private String extensionPrefix;

    @Override
    public String getExtensionPrefix() {
        return extensionPrefix;
    }

    public void setExtensionPrefix(String extensionPrefix) {
        this.extensionPrefix = extensionPrefix;
    }
}
