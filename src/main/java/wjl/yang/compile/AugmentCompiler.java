package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.utils.YangKeyword;

import java.util.List;

/**
 * 处理模块中的augment语句，不是uses里面的augment子句。
 */
class AugmentCompiler {
    void expandAugment(List<YangModule> modules) {
        for (YangModule module : modules) {
            module.getStmt().forEach(YangKeyword.AUGMENT, AugmentCopier::bodyAugment);
        }
    }
}
