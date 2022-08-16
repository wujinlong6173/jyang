package wjl.yang.grammar;

import wjl.yang.model.YangToken;
import wjl.yang.utils.YangTypeKeyword;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * type语句，不同类型需要不同的子句。
 *
 * @author wujinlong
 * @since 2022-8-16
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
        StmtGrammar value = new StmtGrammar(YangTypeKeyword.VALUE, YangToken.STRING);

        // length
        StmtGrammar length = new StmtGrammar(YangTypeKeyword.LENGTH, YangToken.INTEGER, YangToken.STRING);
        length.addSub(errorMessage, 0, 1);
        length.addSub(errorAppTag, 0, 1);
        length.addSub(description, 0, 1);
        length.addSub(reference, 0, 1);

        // fraction-digits
        StmtGrammar fractionDigits = new StmtGrammar(YangTypeKeyword.FRACTION_DIGITS, YangToken.INTEGER);

        // modifier
        StmtGrammar modifier = new StmtGrammar("modifier", YangToken.STRING);
        modifier.setValidValues("invert-match");

        // pattern
        StmtGrammar pattern = new StmtGrammar(YangTypeKeyword.PATTERN, YangToken.STRING);
        pattern.addSub(modifier, 0, 1);
        pattern.addSub(errorMessage, 0, 1);
        pattern.addSub(errorAppTag, 0, 1);
        pattern.addSub(description, 0, 1);
        pattern.addSub(reference, 0, 1);

        // range
        StmtGrammar range = new StmtGrammar(YangTypeKeyword.RANGE, YangToken.STRING);
        range.addSub(errorMessage, 0, 1);
        range.addSub(errorAppTag, 0, 1);
        range.addSub(description, 0, 1);
        range.addSub(reference, 0, 1);

        // enum
        StmtGrammar enumItem = new StmtGrammar(YangTypeKeyword.ENUM, YangToken.IDENTITY, YangToken.STRING);
        enumItem.addSub(ifFeature, 0, -1);
        enumItem.addSub(value, 0, 1);
        enumItem.addSub(status, 0, 1);
        enumItem.addSub(description, 0, 1);
        enumItem.addSub(reference, 0, 1);

        // require-instance
        StmtGrammar requireInstance = new StmtGrammar(YangTypeKeyword.REQUIRE_INSTANCE, YangToken.IDENTITY);
        requireInstance.setValidValues("true", "false");

        // path
        StmtGrammar path = new StmtGrammar(YangTypeKeyword.PATH, YangToken.STRING);

        // position
        StmtGrammar position = new StmtGrammar(YangTypeKeyword.POSITION, YangToken.STRING);

        // bit
        StmtGrammar bit = new StmtGrammar(YangTypeKeyword.BIT, YangToken.IDENTITY);
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
            case YangTypeKeyword.STRING:
                return string;
            case YangTypeKeyword.INT8:
            case YangTypeKeyword.INT16:
            case YangTypeKeyword.INT32:
            case YangTypeKeyword.INT64:
            case YangTypeKeyword.UINT8:
            case YangTypeKeyword.UINT16:
            case YangTypeKeyword.UINT32:
            case YangTypeKeyword.UINT64:
                return numerical;
            case YangTypeKeyword.DECIMAL64:
                return decimal64;
            case YangTypeKeyword.ENUMERATION:
                return enumeration;
            case YangTypeKeyword.BITS:
                return bits;
            case YangTypeKeyword.BINARY:
                return binary;
            case YangTypeKeyword.LEAF_REF:
                return leafref;
            case YangTypeKeyword.IDENTITY_REF:
                return identityref;
            case YangTypeKeyword.INSTANCE_IDENTIFIER:
                return instanceIdentifier;
            case YangTypeKeyword.UNION:
                return union;
            default: // boolean empty and typedef
                return Collections.emptyMap();
        }
    }

    private void sub(Map<String, SubStmtGrammar> target, StmtGrammar stmt, int min, int max) {
        target.put(stmt.getKey(), new SubStmtGrammar(stmt, min, max, 0, target.size()));
    }
}
