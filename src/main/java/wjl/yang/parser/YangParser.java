package wjl.yang.parser;

import wjl.yang.model.YangStmt;
import wjl.yang.model.YangStmtExt;
import wjl.yang.model.YangToken;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Yang语法解析器。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangParser {
    private YangLex lex;
    private int nextToken = 0;

    // 有些语句需要字符串，但经常会省略引号。
    private static final Set<String> STMT_REQUIRE_STRING;
    static {
        STMT_REQUIRE_STRING = new HashSet<>();
        STMT_REQUIRE_STRING.add(YangKeyword.WHEN);
    }

    public YangStmt parse(YangLex lex) throws IOException, YangParseException {
        List<YangStmt> topStmtList = parseFragment(lex);
        if (topStmtList.isEmpty()) {
            return null;
        } else if (topStmtList.size() > 1) {
            throw makeException("one file can define on statement.");
        } else {
            return topStmtList.get(0);
        }
    }

    /**
     * 解析模型片段，一个文件中可以定义多条语句
     *
     * @param lex
     * @return 顶层定义的语句列表
     * @throws IOException
     * @throws YangParseException
     */
    public List<YangStmt> parseFragment(YangLex lex) throws IOException, YangParseException {
        this.lex = lex;
        final List<YangStmt> topStmtList = new ArrayList<>();

        nextToken = lex.yylex();
        while (nextToken != -1) {
            YangStmt top = statement();
            topStmtList.add(top);
        }
        return topStmtList;
    }

    private YangStmt statement() throws YangParseException, IOException {
        YangStmt curr;
        // 解析语句的键值
        if (nextToken == YangToken.IDENTITY) {
            curr = new YangStmt();
            curr.setKey(lex.getString());
            curr.setLine(lex.getLine());
            if (STMT_REQUIRE_STRING.contains(curr.getKey())) {
                lex.require_string();
            }
        } else if (nextToken == YangToken.PREFIX_ID) {
            // 带前缀的关键字，是自定义的扩展关键字
            YangStmtExt ext = new YangStmtExt();
            curr = ext;
            curr.setKey(lex.getString());
            curr.setLine(lex.getLine());
            ext.setExtensionPrefix(getPrefix(curr.getKey()));
        } else {
            throw makeException("require yang keyword");
        }

        // 解析语句的值，部分语句不需要值
        nextToken = lex.yylex();
        if (nextToken == YangToken.STRING) {
            curr.setValueToken(nextToken);
            String str = readStringValue();
            curr.setValue(str);
        } else if (isValue(nextToken)) {
            curr.setValueToken(nextToken);
            curr.setValue(lex.getString());
            nextToken = lex.yylex();
        }

        // 读取分号或者子句
        if (nextToken == YangToken.LEFT_BRACE) {
            nextToken = lex.yylex();
            while (true) {
                if (nextToken == -1) {
                    throw makeException("require right brace");
                } else if (nextToken == YangToken.RIGHT_BRACE) {
                    nextToken = lex.yylex();
                    break;
                } else {
                    YangStmt sub = statement();
                    curr.addSubStatement(sub);
                }
            }
        } else if (nextToken == YangToken.SEMI) {
            nextToken = lex.yylex();
        } else {
            throw makeException("require semicolon or left brace");
        }
        return curr;
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
        if (str.startsWith("\"") || str.startsWith("'")) {
            sb.append(str, 1, str.length() - 1);
        } else {
            sb.append(str.trim());
        }
        while ((nextToken = lex.yylex()) == YangToken.PLUS) {
            nextToken = lex.yylex();
            if (nextToken != YangToken.STRING) {
                break;
            }
            str = lex.getString();
            sb.append(str, 1, str.length() - 1);
        }
        return sb.toString();
    }

    private YangParseException makeException(String msg) {
        return new YangParseException(msg, lex.getLine(), lex.getString());
    }

    private static String getPrefix(String prefixId) {
        return prefixId.substring(0, prefixId.indexOf(':'));
    }
}
