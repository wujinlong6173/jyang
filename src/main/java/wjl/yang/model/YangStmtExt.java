package wjl.yang.model;

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
