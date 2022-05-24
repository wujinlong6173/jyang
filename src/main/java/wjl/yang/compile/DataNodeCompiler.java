package wjl.yang.compile;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;
import wjl.yang.utils.YangKeyword;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 1. 同一名字空间的数据节点不能重名，包括 include 引入的定义。
 * 2. 不同模块通过 augment 添加的数据节点，可以重名。
 */
public class DataNodeCompiler {
    private static final Set<String> DATA_NODE_KEYS;

    static {
        DATA_NODE_KEYS = new HashSet<>();
        DATA_NODE_KEYS.add(YangKeyword.CONTAINER);
        DATA_NODE_KEYS.add(YangKeyword.LIST);
        DATA_NODE_KEYS.add(YangKeyword.LEAF);
        DATA_NODE_KEYS.add(YangKeyword.LEAF_LIST);
        DATA_NODE_KEYS.add(YangKeyword.ANYXML);
        DATA_NODE_KEYS.add(YangKeyword.ANYDATA);
    }

    void compile(List<YangMainModule> modules) {
        for (YangMainModule mainModule : modules) {
            // 合并主模块和子模块定义的模型节点
            Map<String, YangStmt> exists = new HashMap<>();
            for (YangSubModule subModule : mainModule.getSubModules()) {
                containerStmt(subModule.getStmt(), exists);
            }
            containerStmt(mainModule.getStmt(), exists);
        }
    }

    private void containerStmt(YangStmt module, Map<String, YangStmt> exists) {
        if (module == null || module.getSubStatements() == null) {
            return;
        }

        for (YangStmt stmt : module.getSubStatements()) {
            switch (stmt.getKey()) {
                case YangKeyword.CONTAINER:
                case YangKeyword.LIST: {
                    checkConflict(exists, stmt);
                    Map<String, YangStmt> subExists = new HashMap<>();
                    containerStmt(stmt, subExists);
                    break;
                }

                case YangKeyword.LEAF:
                case YangKeyword.LEAF_LIST:
                case YangKeyword.ANYDATA:
                case YangKeyword.ANYXML: {
                    checkConflict(exists, stmt);
                    break;
                }

                case YangKeyword.CHOICE: {
                    choiceStmt(stmt, exists);
                    break;
                }
            }
        }
    }

    // 典型情况是choice包含case；choice直接包含其它类型的语句，相当于switch/case语句的default。
    // 不够是case还是其它类型的语句，都在父语句的名字空间中。
    private void choiceStmt(YangStmt choice, Map<String, YangStmt> exists) {
        if (choice.getSubStatements() == null) {
            return;
        }

        for (YangStmt stmt : choice.getSubStatements()) {
            switch (stmt.getKey()) {
                case YangKeyword.CONTAINER:
                case YangKeyword.LIST: {
                    checkConflict(exists, stmt);
                    Map<String, YangStmt> subExists = new HashMap<>();
                    containerStmt(stmt, subExists);
                    break;
                }

                case YangKeyword.LEAF:
                case YangKeyword.LEAF_LIST:
                case YangKeyword.ANYDATA:
                case YangKeyword.ANYXML: {
                    checkConflict(exists, stmt);
                    break;
                }

                case YangKeyword.CHOICE: {
                    choiceStmt(stmt, exists);
                    break;
                }

                case YangKeyword.CASE: {
                    containerStmt(stmt, exists);
                    break;
                }
            }
        }
    }

    private void checkConflict(Map<String, YangStmt> exists, YangStmt add) {
        String name = add.getValue();
        YangStmt old = exists.putIfAbsent(name, add);
        if (old != null) {
            add.reportError(String.format("conflict with %s.", old.toString()));
        }
    }
}
