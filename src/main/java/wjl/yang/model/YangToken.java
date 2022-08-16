package wjl.yang.model;

/**
 * Yang词法分析产生的单词。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public interface YangToken {
    int IDENTITY = 1;
    int STRING = 2;
    int INTEGER = 3;
    int DATE = 4;
    int DECIMAL = 5;
    int PREFIX_ID = 6;
    int AND = 7;
    int OR = 8;
    int NOT = 9;

    int SEMI = 59; // ;
    int LEFT_BRACE = 123; // {
    int RIGHT_BRACE = 125; // }
    int PLUS = 43; // +

    static String getText(int token) {
        switch(token) {
            case IDENTITY:
                return "identity";
            case STRING:
                return "string";
            case INTEGER:
                return "integer";
            case DATE:
                return "date";
            case DECIMAL:
                return "decimal";
            case PREFIX_ID:
                return "prefix:identity";
            case SEMI:
                return ";";
            case LEFT_BRACE:
                return "{";
            case RIGHT_BRACE:
                return "}";
            case PLUS:
                return "+";
            case 0:
                return "null";
            default:
                return "?";
        }
    }
}
