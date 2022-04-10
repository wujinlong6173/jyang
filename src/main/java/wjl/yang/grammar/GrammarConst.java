package wjl.yang.grammar;

import wjl.yang.model.YangToken;
import wjl.yang.utils.YangKeyword;

class GrammarConst {
    // description
    static final StmtGrammar DESCRIPTION
        = new StmtGrammar(YangKeyword.DESCRIPTION, YangToken.STRING);

    // reference
    static final StmtGrammar REFERENCE
        = new StmtGrammar(YangKeyword.REFERENCE, YangToken.STRING);

    // if-feature
    static final StmtGrammar IF_FEATURE
        = new StmtGrammar(YangKeyword.IF_FEATURE, YangToken.IDENTITY, YangToken.PREFIX_ID, YangToken.STRING);

    // status
    static final StmtGrammar STATUS
        = new StmtGrammar(YangKeyword.STATUS, YangToken.STRING, YangToken.IDENTITY);

    // base
    static final StmtGrammar BASE
        = new StmtGrammar(YangKeyword.BASE, YangToken.IDENTITY, YangToken.PREFIX_ID);

    // error-message
    static final StmtGrammar ERROR_MESSAGE = new StmtGrammar("error-message", YangToken.STRING);

    // error-app-tag
    static final StmtGrammar ERROR_APP_TAG = new StmtGrammar("error-app-tag", YangToken.STRING);

    // units
    static final StmtGrammar UNITS = new StmtGrammar("units", YangToken.STRING);

    // must
    static final StmtGrammar MUST = new StmtGrammar(YangKeyword.MUST, YangToken.STRING);

    // config
    static final StmtGrammar CONFIG = new StmtGrammar(YangKeyword.CONFIG, YangToken.IDENTITY, YangToken.IDENTITY);

    // mandatory
    static final StmtGrammar MANDATORY = new StmtGrammar(YangKeyword.MANDATORY, YangToken.STRING);

    // min-elements
    static final StmtGrammar MIN_ELEMENTS = new StmtGrammar("min-elements", YangToken.INTEGER, YangToken.STRING);

    // max-elements
    static final StmtGrammar MAX_ELEMENTS = new StmtGrammar("max-elements", YangToken.INTEGER, YangToken.STRING);

    // default
    static final StmtGrammar DEFAULT = new StmtGrammar("default", YangToken.INTEGER, YangToken.STRING, YangToken.IDENTITY);

    // unique
    static final StmtGrammar UNIQUE = new StmtGrammar("unique", YangToken.STRING);

    static final StmtGrammar TYPE = new TypeGrammar(YangKeyword.TYPE, YangToken.IDENTITY, YangToken.PREFIX_ID);

    static {
        STATUS.setValidValues("current", "obsolete", "deprecated");

        MUST.addSub(ERROR_MESSAGE, 0, 1);
        MUST.addSub(ERROR_APP_TAG, 0, 1);
        MUST.addSub(DESCRIPTION, 0, 1);
        MUST.addSub(REFERENCE, 0, 1);

        CONFIG.setBooleanValues();

        MANDATORY.setBooleanValues();
    }
}
