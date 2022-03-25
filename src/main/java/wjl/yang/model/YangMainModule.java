package wjl.yang.model;

import java.util.HashMap;
import java.util.Map;

public class YangMainModule extends YangModule {
    // 本模块定义的和包含的特性。
    private Map<String, YangFeature> features = new HashMap<>();

    /**
     * 主模块
     *
     * @param name 主模块的名称
     * @param prefix 主模块的前缀
     * @param version 主模块的版本
     */
    public YangMainModule(String name, String prefix, String version) {
        super(name, prefix, version);
        setMainModule(this);
    }

    public void setFeatures(Map<String, YangFeature> features) {
        this.features = features;
    }

    public Map<String, YangFeature> getFeatures() {
        return features;
    }
}
