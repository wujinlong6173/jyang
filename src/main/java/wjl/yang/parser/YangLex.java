package wjl.yang.parser;
import wjl.yang.model.YangToken;


public class YangLex {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	public final int YYEOF = -1;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public YangLex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public YangLex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private YangLex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int if_feature_expr = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int schema_node_id = 3;
	private final int yy_state_dtrans[] = {
		0,
		14,
		47,
		49
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NOT_ACCEPT,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NOT_ACCEPT,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NOT_ACCEPT,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NOT_ACCEPT,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NOT_ACCEPT,
		/* 40 */ YY_NOT_ACCEPT,
		/* 41 */ YY_NOT_ACCEPT,
		/* 42 */ YY_NOT_ACCEPT,
		/* 43 */ YY_NOT_ACCEPT,
		/* 44 */ YY_NOT_ACCEPT,
		/* 45 */ YY_NOT_ACCEPT,
		/* 46 */ YY_NOT_ACCEPT,
		/* 47 */ YY_NOT_ACCEPT,
		/* 48 */ YY_NOT_ACCEPT,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NOT_ACCEPT,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NOT_ACCEPT,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NOT_ACCEPT,
		/* 59 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"7:9,13,8,7:2,14,7:18,13,7,5,7:4,9,17:2,16,1,7,10,12,15,11,3:9,4,1,7:5,2:26," +
"7,6,7:4,18,2:2,20,2:9,19,21,2:2,22,2,23,2:6,1,7,1,7:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,60,
"0,1:2,2,3,1:4,4,1,5,6,1,7,1,8,1:2,8,9,8:2,1,10,11,12,13,14,15,16,17,18,6,19" +
",20,21,12,22,23,17,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42")[0];

	private int yy_nxt[][] = unpackFromString(43,24,
"1,2,3,4,5,26,5:2,6,31,35,25,5,6,24,38,5:2,3:6,-1:26,3:2,29,-1:5,3:2,-1:6,3:" +
"6,-1:3,56,-1:7,56,33,-1:12,9:7,-1,9:5,-1,9:9,-1:2,11:2,-1:6,11:2,-1:6,11:6," +
"-1:3,12,-1:7,12,-1:12,1,27:15,45,27:7,-1:2,16:2,48,-1:5,16:2,-1:6,16:6,-1:2" +
",20:2,-1:6,20:2,-1:6,20:6,-1:8,6,-1:18,42,-1:7,42,33,-1:12,37:4,7,39,37:17," +
"-1,27:15,46,27:7,-1:2,16:2,48,-1:5,16:2,-1:6,16:4,19,16,-1:2,11,-1:15,11:6," +
"-1:12,33,-1:12,40:5,41,40:2,8,40:14,-1:2,16:2,48,-1:5,16:2,-1:6,16:2,21,16:" +
"3,-1:3,59,-1:6,54,59,33,-1:14,59,-1:7,30,-1:14,16:2,48,-1:5,16:2,-1:6,16:5," +
"22,-1:15,9,10,-1:8,37:7,-1,37:5,-1,37:9,-1,40:7,-1,40:5,-1,40:9,-1:3,52,-1:" +
"7,52,-1:22,54,-1:16,13,-1:7,13,-1:13,27:14,15,27:8,-1,27:14,-1,27:8,1,5,16," +
"5:5,-1,5:4,17,-1,5:2,18,51,53,16,28,16:2,-1:2,20,-1:15,20:6,1,5,16,5:5,-1,5" +
":5,-1,23,5:2,16:6,-1:3,34,-1:7,34,33,-1:13,16:2,48,-1:5,16:2,-1:6,16,32,16:" +
"4,-1:3,43,-1:7,43,-1:14,16:2,48,-1:5,16:2,-1:6,16:3,36,16:2,-1:3,58,-1:7,58" +
",-1:15,44,-1:7,44,-1:15,50,-1:7,50,33,-1:21,55,-1:16,57,-1:7,57,-1:15,59,-1" +
":7,59,33,-1:11");

	public int yylex ()
		throws java.io.IOException, 
YangParseException

		{
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return YYEOF;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return getChar(); }
					case -3:
						break;
					case 3:
						{ return YangToken.IDENTITY; }
					case -4:
						break;
					case 4:
						{ return YangToken.INTEGER; }
					case -5:
						break;
					case 5:
						{
            throw new YangParseException(yyline+1, yy_buffer_start+1, getChar()); }
					case -6:
						break;
					case 6:
						{}
					case -7:
						break;
					case 7:
						{ return YangToken.STRING; }
					case -8:
						break;
					case 8:
						{ return YangToken.STRING; }
					case -9:
						break;
					case 9:
						{}
					case -10:
						break;
					case 10:
						{ yybegin(COMMENT); }
					case -11:
						break;
					case 11:
						{ return YangToken.PREFIX_ID; }
					case -12:
						break;
					case 12:
						{ return YangToken.DECIMAL; }
					case -13:
						break;
					case 13:
						{ return YangToken.DATE; }
					case -14:
						break;
					case 14:
						{}
					case -15:
						break;
					case 15:
						{ yybegin(YYINITIAL); }
					case -16:
						break;
					case 16:
						{ return YangToken.PREFIX_ID; }
					case -17:
						break;
					case 17:
						{}
					case -18:
						break;
					case 18:
						{ return getChar(); }
					case -19:
						break;
					case 19:
						{ return YangToken.OR; }
					case -20:
						break;
					case 20:
						{ return YangToken.PREFIX_ID; }
					case -21:
						break;
					case 21:
						{ return YangToken.AND; }
					case -22:
						break;
					case 22:
						{ return YangToken.NOT; }
					case -23:
						break;
					case 23:
						{ return getChar(); }
					case -24:
						break;
					case 25:
						{ return YangToken.INTEGER; }
					case -25:
						break;
					case 26:
						{
            throw new YangParseException(yyline+1, yy_buffer_start+1, getChar()); }
					case -26:
						break;
					case 27:
						{}
					case -27:
						break;
					case 28:
						{ return YangToken.PREFIX_ID; }
					case -28:
						break;
					case 30:
						{ return YangToken.INTEGER; }
					case -29:
						break;
					case 31:
						{
            throw new YangParseException(yyline+1, yy_buffer_start+1, getChar()); }
					case -30:
						break;
					case 32:
						{ return YangToken.PREFIX_ID; }
					case -31:
						break;
					case 34:
						{ return YangToken.INTEGER; }
					case -32:
						break;
					case 35:
						{
            throw new YangParseException(yyline+1, yy_buffer_start+1, getChar()); }
					case -33:
						break;
					case 36:
						{ return YangToken.PREFIX_ID; }
					case -34:
						break;
					case 38:
						{
            throw new YangParseException(yyline+1, yy_buffer_start+1, getChar()); }
					case -35:
						break;
					case 50:
						{ return YangToken.INTEGER; }
					case -36:
						break;
					case 51:
						{ return YangToken.PREFIX_ID; }
					case -37:
						break;
					case 53:
						{ return YangToken.PREFIX_ID; }
					case -38:
						break;
					case 56:
						{ return YangToken.INTEGER; }
					case -39:
						break;
					case 59:
						{ return YangToken.INTEGER; }
					case -40:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
