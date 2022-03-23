package wjl.yang.parser;

import wjl.yang.model.YangStmt;
import wjl.yang.model.YangToken;

import java.io.IOException;
import java.util.Stack;

public class YangParser {
    private final Stack<YangStmt> stack = new Stack<>();
    private YangLex lex;
    private int cacheToken = 0;

    public YangStmt parse(YangLex lex) throws IOException, YangParseException {
        this.lex = lex;
        this.stack.clear();

        YangStmt parent;
        YangStmt curr;
        int token;
        while (true) {
            // read key
            token = lex.yylex();
            if (token == -1) {
                if (stack.isEmpty()) {
                    return null;
                } else {
                    throw makeException("require right brace");
                }
            }

            if (token == YangToken.IDENTITY) {
                curr = new YangStmt();
                curr.setKey(lex.getString());
                curr.setLine(lex.getLine());
            } else if (token == YangToken.RIGHT_BRACE) {
                if (stack.isEmpty()) {
                    throw makeException("not matched right brace");
                }
                curr = stack.pop();
                if (stack.isEmpty()) {
                    checkEndOfFile();
                    return curr;
                }
                parent = stack.peek();
                parent.addSubStatement(curr);
                continue;
            } else {
                throw makeException("require wjl.yang keyword");
            }

            // read value
            token = lex.yylex();
            if (token == YangToken.STRING) {
                curr.setValueToken(token);
                String str = readStringValue();
                curr.setValue(str);
            } else if (isValue(token)) {
                curr.setValueToken(token);
                curr.setValue(lex.getString());
            } else {
                throw makeException("require wjl.yang value");
            }

            // read semicolon or left brace
            if (cacheToken == 0) {
                token = lex.yylex();
            } else {
                token = cacheToken;
                cacheToken = 0;
            }
            if (token == YangToken.SEMI) {
                if (stack.isEmpty()) {
                    checkEndOfFile();
                    return curr;
                }
                parent = stack.peek();
                parent.addSubStatement(curr);
            } else if (token == YangToken.LEFT_BRACE) {
                stack.push(curr);
            } else {
                throw makeException("require semicolon or left brace");
            }
        }
    }

    private boolean isValue(int token) {
        return token == YangToken.IDENTITY
            || token == YangToken.STRING
            || token == YangToken.INTEGER
            || token == YangToken.DATE
            || token == YangToken.DECIMAL
            || token == YangToken.PREFIX_ID;
    }

    private String readStringValue() throws IOException, YangParseException {
        StringBuilder sb = new StringBuilder();
        String str = lex.getString();
        sb.append(str.substring(1, str.length() - 1));
        int token;
        while (true) {
            token = lex.yylex();
            if (token != YangToken.PLUS) {
                cacheToken = token;
                break;
            }
            token = lex.yylex();
            if (token != YangToken.STRING) {
                cacheToken = token;
                break;
            }
            str = lex.getString();
            sb.append(str.substring(1, str.length() - 1));
        }
        return sb.toString();
    }

    void checkEndOfFile() throws IOException, YangParseException {
        int token = lex.yylex();
        if (token != -1) {
            throw makeException("expect end of file");
        }
    }

    private YangParseException makeException(String msg) {
        return new YangParseException(msg, lex.getLine(), lex.getString());
    }
}
