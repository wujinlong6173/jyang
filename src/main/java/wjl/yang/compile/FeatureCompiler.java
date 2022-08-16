package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.IfFeatureExprParser;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.UiGraph;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * 处理 feature 和 if-feature 语句。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
class FeatureCompiler extends DefineAndUseCompiler {
    private final IfFeatureExprParser parser = new IfFeatureExprParser();
    private UiGraph<YangStmt, Void> featureDepends;

    FeatureCompiler() {
        super(YangKeyword.FEATURE, YangKeyword.IF_FEATURE);
    }

    /**
     *
     * @param modules 主模块和子模块。
     */
    void collectFeatures(List<YangModule> modules) {
        featureDepends = new UiGraph<>();
        searchDefineInModules(modules);
        // 检查 if-feature 中表达式的格式，检查表达式中用到的特性有没有定义
        searchUseInModules(modules, false);
        CompileUtil.reportCircularDependency(featureDepends);
    }

    @Override
    protected void onUse(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeDefines) {
        YangLex lex = new YangLex(new StringReader(stmt.getValue()));
        try {
            parser.parse(lex);
            for (String term : parser.getStack()) {
                switch (term) {
                    case "!":
                    case "v":
                    case "^":
                        break;
                    default: {
                        YangStmt target = checkFeatureDefined(stmt, term);
                        if (parentDefine != null && target != null) {
                            featureDepends.addEdge(parentDefine, target, null);
                        }
                        break;
                    }
                }
            }
        } catch (IOException | YangParseException err) {
            stmt.reportError(err.getMessage());
        }
    }

    private YangStmt checkFeatureDefined(YangStmt ifFea, String prefixId) {
        YangModule module = ifFea.getOriModule();
        ModuleAndIdentify mi = module.separate(prefixId, true);
        YangStmt target = null;
        if (mi == null) {
            ifFea.reportError(prefixId + " invalid prefix.");
        } else {
            target = getDefine(mi);
            if (target == null) {
                ifFea.reportError(prefixId + " undefined feature.");
            }
        }
        return target;
    }
}
