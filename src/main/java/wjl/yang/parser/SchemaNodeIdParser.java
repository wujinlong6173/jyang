package wjl.yang.parser;

import wjl.yang.model.YangToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SchemaNodeIdParser {
    public static List<String> parse(String pathStr, boolean absolute) throws IOException, YangParseException {
        YangLex lex = new YangLex(new StringReader(pathStr));
        lex.schema_node_id();
        List<String> path = new ArrayList<>();

        int nextToken = lex.yylex();
        if (absolute) {
            if (nextToken == '/') {
                nextToken = lex.yylex();
            } else {
                throw new YangParseException("require absolute schema id.");
            }
        } else if (nextToken == '/') {
            throw new YangParseException("require descendant schema id.");
        }

        while (nextToken == YangToken.PREFIX_ID) {
            path.add(lex.getString());
            nextToken = lex.yylex();
            if (nextToken == -1) {
                return path;
            } else if (nextToken != '/') {
                throw new YangParseException("require /", lex.getLine(), lex.getString());
            } else {
                nextToken = lex.yylex();
            }
        }
        throw new YangParseException("require node identifier", lex.getLine(), lex.getString());
    }
}
