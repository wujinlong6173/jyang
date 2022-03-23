package wjl.yang.parser;

import wjl.yang.model.YangToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchemaNodeIdParser {
    private final List<String> stack = new ArrayList<>();

    /**
     *
     * @param lex
     * @return 是不是绝对路径，以斜杠开头的是绝对路径
     * @throws IOException
     * @throws YangParseException
     */
    public boolean parse(YangLex lex) throws IOException, YangParseException {
        lex.schema_node_id();
        boolean absolute = false;
        stack.clear();

        int nextToken = lex.yylex();
        if (nextToken == '/') {
            absolute = true;
            nextToken = lex.yylex();
        }
        while (nextToken == YangToken.PREFIX_ID) {
            stack.add(lex.getString());
            nextToken = lex.yylex();
            if (nextToken == -1) {
                return absolute;
            } else if (nextToken != '/') {
                throw new YangParseException("require /", lex.getLine(), lex.getString());
            } else {
                nextToken = lex.yylex();
            }
        }
        throw new YangParseException("require node identifier", lex.getLine(), lex.getString());
    }

    public List<String> getStack() {
        return stack;
    }
}
