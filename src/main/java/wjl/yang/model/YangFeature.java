package wjl.yang.model;

/**
 * 对应 feature 语句。
 */
public class YangFeature {
    /**
     * 特性的名称
     */
    private final String name;

    private final YangStmt feature;

    /**
     * 对应 feature 语句中的 if-feature语句。
     */
    private final YangStmt ifFeature;

    /**
     * 求值过程使用，空表示还未求值。
     */
    private Boolean value;

    public YangFeature(String name, YangStmt feature, YangStmt ifFeature) {
        this.name = name;
        this.feature = feature;
        this.ifFeature = ifFeature;
    }

    public String getName() {
        return name;
    }

    public YangStmt getFeature() {
        return feature;
    }

    public YangStmt getIfFeature() {
        return ifFeature;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return feature.toString();
    }
}
