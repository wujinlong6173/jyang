package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.utils.YangKeyword;

import java.util.List;

class AugmentCompiler {
    void expandAugment(List<YangModule> modules) {
        for (YangModule module : modules) {
            module.getStmt().forEach(YangKeyword.AUGMENT, AugmentCopier::bodyAugment);
        }
    }
}
