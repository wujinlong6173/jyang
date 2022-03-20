package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangFeature;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;
import wjl.yang.parser.IfFeatureExprParser;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理 feature 和 if-feature 语句。
 */
class FeatureCompiler {
    /**
     *
     * @param modules 主模块和子模块。
     */
    static void collectFeatures(List<YangModule> modules) {
        // 搜索每个模块、子模块定义的特性。
        Map<YangModule, Map<String, YangFeature>> moduleToFeatures = new HashMap<>();
        for (YangModule module : modules) {
            moduleToFeatures.put(module, searchOneModule(module));
        }

        // 复制子模块定义的特性
        for (YangModule module : modules) {
            module.setFeatures(copyIncludedFeatures(module, moduleToFeatures));
        }
    }

    private static Map<String, YangFeature> searchOneModule(YangModule module) {
        Map<String, YangFeature> featureMap = new HashMap<>();
        module.getStmt().forEach(YangKeyword.FEATURE, (fea) -> {
            String name = fea.getValue();
            YangFeature exist = featureMap.get(name);
            if (exist != null) {
                module.addError(fea, " is already defined in " + exist.toString());
            } else {
                YangStmt ifFea = fea.searchOne(YangKeyword.IF_FEATURE);
                featureMap.put(name, new YangFeature(name, fea, ifFea));
            }
        });
        return featureMap;
    }

    private static Map<String, YangFeature> copyIncludedFeatures(YangModule module,
        Map<? super YangModule, Map<String, YangFeature>> moduleToFeatures) {
        Map<String, YangFeature> ret = new HashMap<>();
        Map<String, YangFeature> temp = moduleToFeatures.get(module);
        if (temp != null) {
            ret.putAll(temp);
        }

        for (YangSubModule sub : module.getSubModules()) {
            temp = moduleToFeatures.get(sub);
            if (temp != null) {
                for (YangFeature feature : temp.values()) {
                    YangFeature exist = ret.get(feature.getName());
                    if (exist != null) {
                        module.addError(feature.getFeature(), String.format(" is already defined in %s",
                            exist.getFeature().toString()));
                    } else {
                        ret.put(feature.getName(), feature);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 检查 if-feature 中表达式的格式，检查表达式中用到的特性有没有定义
     */
    static void checkIfFeatures(List<YangModule> modules) {
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

    private static void checkFeatureDefined(YangStmt ifFea, String prefixId) {
        YangModule module = ifFea.getOriModule();
        ModuleAndIdentify mi = module.separate(prefixId);
        if (mi == null) {
            module.addError(ifFea, prefixId + " invalid prefix.");
        } else if (!mi.getModule().getFeatures().containsKey(mi.getIdentify())) {
            module.addError(ifFea, prefixId + " undefined feature.");
        }
    }
}
