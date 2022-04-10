package wjl.yang.grammar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * deviate add ; deviate delete ; deviate-replace ; deviate-not-supported
 */
class DeviateGrammar extends StmtGrammar {
    private final Map<String, Map<String, SubStmtGrammar>> subStmtForTypes;

    DeviateGrammar(String key, int... validTokens) {
        super(key, validTokens);
        setValidValues("add", "delete", "replace", "not-supported");

        StmtGrammar units = GrammarConst.UNITS;
        StmtGrammar must = GrammarConst.MUST;
        StmtGrammar config = GrammarConst.CONFIG;
        StmtGrammar mandatory = GrammarConst.MANDATORY;
        StmtGrammar minElements = GrammarConst.MIN_ELEMENTS;
        StmtGrammar maxElements = GrammarConst.MAX_ELEMENTS;
        StmtGrammar defaults = GrammarConst.DEFAULT;
        StmtGrammar unique = GrammarConst.UNIQUE;
        StmtGrammar type = GrammarConst.TYPE;

        subStmtForTypes = new HashMap<>();

        StmtGrammar add = new StmtGrammar("add");
        add.addSub(units, 0, 1);
        add.addSub(must, 0, -1);
        add.addSub(defaults, 0, -1);
        add.addSub(config, 0, 1);
        add.addSub(mandatory, 0, 1);
        add.addSub(minElements, 0, 1);
        add.addSub(maxElements, 0, 1);
        register(add);

        StmtGrammar delete = new StmtGrammar("delete");
        delete.addSub(units, 0, 1);
        delete.addSub(must, 0, -1);
        delete.addSub(unique, 0, -1);
        delete.addSub(defaults, 0, -1);
        register(delete);

        StmtGrammar replace = new StmtGrammar("replace");
        replace.addSub(type, 0, 1);
        replace.addSub(units, 0, 1);
        replace.addSub(defaults, 0, -1);
        replace.addSub(config, 0, 1);
        replace.addSub(mandatory, 0, 1);
        replace.addSub(minElements, 0, 1);
        replace.addSub(maxElements, 0, 1);
        register(replace);

        StmtGrammar notSupported = new StmtGrammar("not-supported");
        register(notSupported);
    }

    @Override
    Map<String, SubStmtGrammar> getSubStatements(String value) {
        Map<String, SubStmtGrammar> found = subStmtForTypes.get(value);
        return found != null ? found : Collections.emptyMap();
    }

    private void register(StmtGrammar type) {
        subStmtForTypes.put(type.getKey(), type.getSubStatements(null));
    }
}
