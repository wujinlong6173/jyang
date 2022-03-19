package wjl.yang.parser;

import wjl.yang.model.YangToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析 if-feature 语句中的表达式，转换成后缀表达式，用 ^ v ! 代表与或非运算符。
 */
public class IfFeatureExprParser {
    private final List<String> stack = new ArrayList<>();
    private YangLex lex;
    private int nextToken;

    public void parse(YangLex lex) throws IOException, YangParseException {
        lex.if_feature_expr();
        this.lex = lex;
        stack.clear();
        nextToken = lex.yylex();
        expr();
        if (nextToken != -1) {
            throw new YangParseException("unexpected token", lex.getLine(), lex.getString());
        }
    }

    public List<String> getStack() {
        return stack;
    }

    private void expr() throws IOException, YangParseException {
        term();
        while (nextToken == YangToken.OR) {
            nextToken = lex.yylex();
            term();
            stack.add("v");
        }
    }

    private void term() throws IOException, YangParseException {
        factor();
        while (nextToken == YangToken.AND) {
            nextToken = lex.yylex();
            factor();
            stack.add("^");
        }
    }

    private void factor() throws IOException, YangParseException {
        if (nextToken == YangToken.NOT) {
            nextToken = lex.yylex();
            factor();
            stack.add("!");
        } else if (nextToken == '(') {
            nextToken = lex.yylex();
            expr();
            if (nextToken == ')') {
                nextToken = lex.yylex();
            }
        } else if (nextToken == YangToken.PREFIX_ID) {
            stack.add(lex.getString());
            nextToken = lex.yylex();
        } else {
            throw new YangParseException("unexpected token", lex.getLine(), lex.getString());
        }
    }
}
