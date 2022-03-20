package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangFeature;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.IfFeatureExprParser;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理 feature 和 if-feature 语句。
 */
class FeatureCompiler {
    private Map<YangModule, Map<String, YangStmt>> moduleToFeatures;

    /**
     *
     * @param modules 主模块和子模块。
     */
    void collectFeatures(List<YangModule> modules) {
        moduleToFeatures = CompileUtil.collectSpecialStmt(modules, YangKeyword.FEATURE);
        checkIfFeatures(modules);

        for (YangModule module : modules) {
            if (module instanceof YangMainModule) {
                convertFeatures((YangMainModule)module);
            }
        }
    }

    /**
     * 检查 if-feature 中表达式的格式，检查表达式中用到的特性有没有定义
     */
    void checkIfFeatures(List<YangModule> modules) {
        IfFeatureExprParser parser = new IfFeatureExprParser();
        for (YangModule module : modules) {
            module.getStmt().iterateAll((stmt) -> {
                if (YangKeyword.IF_FEATURE.equals(stmt.getKey())) {
                    YangLex lex = new YangLex(new StringReader(stmt.getValue()));
                    try {
                        parser.parse(lex);
                        for (String term : parser.getStack()) {
                            switch (term) {
                                case "!":
                                case "v":
                                case "^":
                                    break;
                                default:
                                    checkFeatureDefined(stmt, term);
                                    break;
                            }
                        }
                    } catch (IOException | YangParseException err) {
                        module.addError(stmt, err.getMessage());
                    }
                }
            });

        }
    }

    private void checkFeatureDefined(YangStmt ifFea, String prefixId) {
        YangModule module = ifFea.getOriModule();
        ModuleAndIdentify mi = module.separate(prefixId, true);
        if (mi == null) {
            module.addError(ifFea, prefixId + " invalid prefix.");
        } else {
            Map<String, YangStmt> features = moduleToFeatures.get(mi.getModule());
            if (features == null || !features.containsKey(mi.getIdentify())) {
                module.addError(ifFea, prefixId + " undefined feature.");
            }
        }
    }

    private void convertFeatures(YangMainModule module) {
        Map<String, YangStmt> features = moduleToFeatures.get(module);
        if (features == null) {
            // 不可能发生的情况
            module.setFeatures(Collections.emptyMap());
        } else {
            Map<String, YangFeature> temp = new HashMap<>();
            for (Map.Entry<String, YangStmt> entry : features.entrySet()) {
                YangStmt ifFea = entry.getValue().searchOne(YangKeyword.IF_FEATURE);
                temp.put(entry.getKey(), new YangFeature(entry.getKey(), entry.getValue(), ifFea));
            }
            module.setFeatures(temp);
        }
    }
}
