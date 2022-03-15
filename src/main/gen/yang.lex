package wjl.yang.parser;

%%

%public
%class YangLex
%integer
%line

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
%}

%eofval{
    return -1;
%eofval}

%state COMMENT

ALPHA = [A-Za-z]
CRLF  = [\r]?\n
WSP    = \x20|\x09|{CRLF}
ID    = {ALPHA}[A-Za-z0-9-]*

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
<YYINITIAL>    "/*"                       { yybegin(COMMENT); }

<COMMENT>     ([^*]|[*][^/]|{CRLF})*  {}
<COMMENT>     "*/"                        { yybegin(YYINITIAL); }

