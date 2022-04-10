package wjl.yang.grammar;

import wjl.yang.model.YangToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * type语句，不同类型需要不同的子句。
 */
class TypeGrammar extends StmtGrammar {
    private final Map<String, SubStmtGrammar> numerical;
    private final Map<String, SubStmtGrammar> string;
    private final Map<String, SubStmtGrammar> decimal64;
    private final Map<String, SubStmtGrammar> enumeration;
    private final Map<String, SubStmtGrammar> bits;
    private final Map<String, SubStmtGrammar> binary;
    private final Map<String, SubStmtGrammar> leafref;
    private final Map<String, SubStmtGrammar> identityref;
    private final Map<String, SubStmtGrammar> instanceIdentifier;
    private final Map<String, SubStmtGrammar> union;

    TypeGrammar(String key, int... validTokens) {
        super(key, validTokens);

        StmtGrammar description = GrammarConst.DESCRIPTION;
        StmtGrammar reference = GrammarConst.REFERENCE;
        StmtGrammar ifFeature = GrammarConst.IF_FEATURE;
        StmtGrammar status = GrammarConst.STATUS;
        StmtGrammar base = GrammarConst.BASE;
        StmtGrammar errorMessage = GrammarConst.ERROR_MESSAGE;
        StmtGrammar errorAppTag = GrammarConst.ERROR_APP_TAG;

        // value
        StmtGrammar value = new StmtGrammar("value", YangToken.STRING);

        // length
        StmtGrammar length = new StmtGrammar("length", YangToken.INTEGER, YangToken.STRING);
        length.addSub(errorMessage, 0, 1);
        length.addSub(errorAppTag, 0, 1);
        length.addSub(description, 0, 1);
        length.addSub(reference, 0, 1);

        // fraction-digits
        StmtGrammar fractionDigits = new StmtGrammar("fraction-digits", YangToken.INTEGER);

        // modifier
        StmtGrammar modifier = new StmtGrammar("modifier", YangToken.STRING);
        modifier.setValidValues("invert-match");

        // pattern
        StmtGrammar pattern = new StmtGrammar("pattern", YangToken.STRING);
        pattern.addSub(modifier, 0, 1);
        pattern.addSub(errorMessage, 0, 1);
        pattern.addSub(errorAppTag, 0, 1);
        pattern.addSub(description, 0, 1);
        pattern.addSub(reference, 0, 1);

        // range
        StmtGrammar range = new StmtGrammar("range", YangToken.STRING);
        range.addSub(errorMessage, 0, 1);
        range.addSub(errorAppTag, 0, 1);
        range.addSub(description, 0, 1);
        range.addSub(reference, 0, 1);

        // enum
        StmtGrammar enumItem = new StmtGrammar("enum", YangToken.IDENTITY, YangToken.STRING);
        enumItem.addSub(ifFeature, 0, -1);
        enumItem.addSub(value, 0, 1);
        enumItem.addSub(status, 0, 1);
        enumItem.addSub(description, 0, 1);
        enumItem.addSub(reference, 0, 1);

        // require-instance
        StmtGrammar requireInstance = new StmtGrammar("require-instance", YangToken.IDENTITY);
        requireInstance.setValidValues("true", "false");

        // path
        StmtGrammar path = new StmtGrammar("path", YangToken.STRING);

        // position
        StmtGrammar position = new StmtGrammar("position", YangToken.STRING);

        // bit
        StmtGrammar bit = new StmtGrammar("bit", YangToken.IDENTITY);
        bit.addSub(ifFeature, 0, -1);
        bit.addSub(position, 0, 1);
        bit.addSub(status, 0, 1);
        bit.addSub(description, 0, 1);
        bit.addSub(reference, 0, 1);

        // for each type
        numerical = new HashMap<>();
        sub(numerical, range, 0, 1);

        decimal64 = new HashMap<>();
        sub(decimal64, fractionDigits, 1, 1);
        sub(decimal64, range, 0, 1);

        string = new HashMap<>();
        sub(string, length, 0, 1);
        sub(string, pattern, 0, -1);

        enumeration = new HashMap<>();
        sub(enumeration, enumItem, 1, -1);

        leafref = new HashMap<>();
        sub(leafref, path, 1, 1);
        sub(leafref, requireInstance, 0, 1);

        identityref = new HashMap<>();
        sub(identityref, base, 1, -1);

        instanceIdentifier = new HashMap<>();
        sub(instanceIdentifier, requireInstance, 0, 1);

        bits = new HashMap<>();
        sub(bits, bit, 1, -1);

        union = new HashMap<>();
        sub(union, this, 1, -1);

        binary = new HashMap<>();
        sub(binary, length, 0, 1);
    }

    @Override
    Map<String, SubStmtGrammar> getSubStatements(String value) {
        switch (value) {
            case "string":
                return string;
            case "int8":
            case "int16":
            case "int32":
            case "int64":
            case "uint8":
            case "uint16":
            case "uint32":
            case "uint64":
                return numerical;
            case "decimal64":
                return decimal64;
            case "enumeration":
                return enumeration;
            case "bits":
                return bits;
            case "binary":
                return binary;
            case "leafref":
                return leafref;
            case "identityref":
                return identityref;
            case "instance-identifier":
                return instanceIdentifier;
            case "union":
                return union;
            default: // boolean empty and typedef
                return Collections.emptyMap();
        }
    }

    private void sub(Map<String, SubStmtGrammar> target, StmtGrammar stmt, int min, int max) {
        target.put(stmt.getKey(), new SubStmtGrammar(stmt, min, max, 0, target.size()));
    }
}
