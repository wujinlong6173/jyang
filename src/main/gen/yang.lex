package wjl.yang.parser;

import wjl.yang.model.YangToken;

%%

%public
%class YangLex
%integer
%line
%yylexthrow{
YangParseException
%yylexthrow}

%{
    public char getChar() {
        return yy_buffer[yy_buffer_start];
    }

    public String getString() {
        return new String(yy_buffer, yy_buffer_start, yy_buffer_end - yy_buffer_start);
    }

    public int getLine() {
        return yyline + 1;
    }

    public void if_feature_expr() {
        yybegin(if_feature_expr);
    }

    public void schema_node_id() {
        yybegin(schema_node_id);
    }

    private void skipComment() throws java.io.IOException {
        boolean hasStar = false;
        int ch;
        while (true) {
            ch = yy_advance();
            if (hasStar && ch == '/') return;
            if (ch == YY_EOF) return;
            if (ch == '\n') yy_mark_start();
            hasStar = ch == '*';
        }
    }
%}

%eofval{
    return -1;
%eofval}

%state COMMENT
%state if_feature_expr
%state schema_node_id

ALPHA = [_A-Za-z\.]
CRLF = [\r]?\n
SP = \x20|\x09
WSP = \x20|\x09|{CRLF}
ID = {ALPHA}[A-Za-z0-9\-_.]*

%%

<YYINITIAL>    [\{\};+]                  { return getChar(); }
<YYINITIAL>    {ID}                      { return YangToken.IDENTITY; }
<YYINITIAL>    ({ID}):({ID})             { return YangToken.PREFIX_ID; }
<YYINITIAL>    \"(\\.|[^\"\\])*\"        { return YangToken.STRING; }
<YYINITIAL>    \'(\\.|[^\'\\])*\'        { return YangToken.STRING; }
<YYINITIAL>    -?(0|([1-9][0-9]*))           { return YangToken.INTEGER; }
<YYINITIAL>    -?(0|([1-9][0-9]*))"."[0-9]+  { return YangToken.DECIMAL; }
<YYINITIAL>    [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]  { return YangToken.DATE; }
<YYINITIAL>    {WSP}                      {}
<YYINITIAL>    //.*                       {}
<YYINITIAL>    "/*"                       { skipComment(); }

<if_feature_expr>   [()]                      { return getChar(); }
<if_feature_expr>   "and"                     { return YangToken.AND; }
<if_feature_expr>   "or"                      { return YangToken.OR; }
<if_feature_expr>   "not"                     { return YangToken.NOT; }
<if_feature_expr,schema_node_id>   {ID}                      { return YangToken.PREFIX_ID; }
<if_feature_expr,schema_node_id>   ({ID}):({ID})             { return YangToken.PREFIX_ID; }
<if_feature_expr>   {SP}                      {}

<schema_node_id>   [/]                        { return getChar(); }

<if_feature_expr,schema_node_id,YYINITIAL>      .       {
            throw new YangParseException(yyline+1, yy_buffer_start+1, getChar()); }