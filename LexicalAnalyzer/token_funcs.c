//以下の書籍を大いに参考にしています
//林晴比古, 「明快入門 コンパイラインタプリタ開発」, ソフトバンク クリエイティブ株式会社, 第1刷.
#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>
#include "token_prots.h"

#define MAIN_BUFF_SIZ 10000
#define BUFF_SIZ 64

char main_buff[MAIN_BUFF_SIZ + 1];
int curr_idx;
int ch;
int row, col;
tkn_kind kind_tbl[256];
keyword key_tbl[] = {
	{"if", 		If},		{"else",	Else},
	{"while",	While},		{"for",		For},
	{"(",		Lparen},	{")",		Rparen},
	{"{",		Lbrace},	{"}",		Rbrace},
	{"[",		Lbracket},	{"]",		Rbracket},
	{"+",		Plus},		{"-",		Minus},
	{"*",		Multi},		{"/",		Div},
	{"%",		Mod},		{"!",		Not},
	{"==",		Equal},		{"!=", 		NotEq},
	{"&&",		And},		{"||",		Or},
	{"<",		Less},		{"<=",		LessEq},
	{">",		Great},		{">=",		GreatEq},
	{"=",		Assign},	{"#",		Sharp},
	{",",		Comma},		{".",		Dot},
	{":",		Colon},		{";",		Semicolon},
	{"?",		Question},	{"@",		AtSign},
	{"++",		Inc},		{"--",		Dec},
	{"&",		Amp},		{"|",		Pipe},

	{"",		EndList}
};

void load_from_array(char arr[]) {
	int i;
	if(strlen(arr) > MAIN_BUFF_SIZ - 1)
		err_exit("over the buffer size");
	for(i = 0; i < strlen(arr); i++)
		main_buff[i] = arr[i];
	main_buff[i] = '\0';
}

void load_from_file(char* filename) {
	FILE *fp;
	int c, i;
	if((fp = fopen(filename, "r")) == NULL)
		err_exit("failed to open the file");
	i = 0;
	while((c = fgetc(fp)) != -1 && i < MAIN_BUFF_SIZ)
		main_buff[i++] = c;
	main_buff[i] = '\0';
}

void init(void) {
	curr_idx = 0;
	ch = ' ';
	row = 1; col = 0;
	init_kind_tbl();
}

void init_kind_tbl(void) {
	int i;
	for(i = 0; i < 256; i++) kind_tbl[i] = Other;
	for(i = '0'; i <= '9'; i++) kind_tbl[i] = Digit;
	for(i = 'a'; i <= 'z'; i++) kind_tbl[i] = Letter;
	for(i = 'A'; i <= 'Z'; i++) kind_tbl[i] = Letter;
	kind_tbl['_'] = Letter;
	kind_tbl['('] = Lparen; kind_tbl[')'] = Rparen;
	kind_tbl['{'] = Lbrace; kind_tbl['}'] = Rbrace;
	kind_tbl['['] = Lbracket; kind_tbl[']'] = Rbracket;
	kind_tbl['<'] = Less; kind_tbl['>'] = Great;
	kind_tbl['!'] = Not;
	kind_tbl['+'] = Plus; kind_tbl['-'] = Minus;
	kind_tbl['*'] = Multi; kind_tbl['/'] = Div;
	kind_tbl['%'] = Mod;
	kind_tbl[':'] = Colon; kind_tbl[';'] = Semicolon;
	kind_tbl['?'] = Question;
	kind_tbl['='] = Assign; kind_tbl['#'] = Sharp;
	kind_tbl['\\'] = Yen;
	kind_tbl[','] = Comma; kind_tbl['.'] = Dot;
	kind_tbl['\''] = SngQ; kind_tbl['"'] = DblQ;
	kind_tbl['@'] = AtSign;
	kind_tbl['&'] = Amp; kind_tbl['|'] = Pipe;
}

int next_char(void) {
	col++;
	if(curr_idx < MAIN_BUFF_SIZ && curr_idx < strlen(main_buff)) {
		int c = main_buff[curr_idx];
		if(c == '\n') {
			row++;
			col = 0;
		}
		else if(c == '\0')
			return -1;
		curr_idx++;
		return c;
	}
	return -1;
}

token next_token(void) {
	token tkn = get_default_token();
	char *p = tkn.text, *p_max = p + TKN_TEXT_SIZ;
	int n;

	while(isspace(ch)) ch = next_char();

	if(ch < 0)
		return tkn;

	switch(kind_tbl[ch]) {

		case Letter:
			for(; (kind_tbl[ch] == Letter || kind_tbl[ch] == Digit) && p < p_max; ch = next_char())
				*p++ = ch;
			*p = '\0';
			break;

		case Digit:
			for(n = 0; kind_tbl[ch] == Digit; ch = next_char())
				n = n * 10 + (ch - '0');
			snprintf(p, TKN_TEXT_SIZ, "%d", n);
			tkn.kind = Num;
			break;

		case SngQ:
			*p++ = next_char();
			if(*(p - 1) == '\\')
				*p++ = next_char();
			*p = '\0';
			ch = next_char();
			if(kind_tbl[ch] != SngQ)
				err_exit("missing '\''");
			tkn.kind = Char;
			ch = next_char();
			break;

		case DblQ:
			for(ch = next_char(); kind_tbl[ch] != DblQ && p < p_max; ch = next_char()) {
				*p++ = ch;
				if(ch == '\\')
					*p++ = next_char();
			}
			*p = '\0';
			if(kind_tbl[ch] != DblQ)
				err_exit("missing '\"'");
			tkn.kind = String;
			ch = next_char();
			break;

		case Div:
			ch = next_char();
			if(kind_tbl[ch] == Div) {
				for(ch = next_char(); ch != '\n' && p < p_max; ch = next_char())
					*p++ = ch;
				*p = '\0';
				if(ch != '\n')
					err_exit("invalid use of \"//...\"");
				tkn.kind = SngCmnt;
				ch = next_char();
			}
			else if(kind_tbl[ch] == Multi) {
				for(ch = next_char(); p < p_max; ch = next_char()) {
					if(kind_tbl[ch] == Multi) {
						ch = next_char();
						if(kind_tbl[ch] == Div)
							break;
						*p++ = '*';
					}
					*p++ = ch;
				}
				if(kind_tbl[ch] != Div)
					err_exit("invalid use of \"/*...*/\"");
				tkn.kind = MltCmnt;
				ch = next_char();
			}
			break;

		default:
			*p++ = ch;
			ch = next_char();
			if(is_ope2(*(p - 1), ch)) {
				*p++ = ch;
				ch = next_char();
			}
			*p = '\0';
	}

	if(tkn.kind == NulKind) set_kind(&tkn);
	if(tkn.kind == Other)
		err_exit("invalid token found");

	return tkn;
}

token get_default_token(void) {
	token tkn;
	tkn.text[0] = '\0';
	tkn.kind = NulKind;
	return tkn;
}

int is_ope2(int c1, int c2) {
	const char* target = " <= >= == != && || ++ -- ";
	char s[] = "    ";
	s[1] = c1; s[2] = c2;
	return strstr(target, s) != NULL;
}

void set_kind(token *tkn) {
	int i;
	tkn->kind = Other;
	for(i = 0; key_tbl[i].kind != EndList; i++)
		if(strcmp(tkn->text, key_tbl[i].text) == 0) {
			tkn->kind = key_tbl[i].kind;
			return;
		}
	if(kind_tbl[tkn->text[0]] == Letter) tkn->kind = Ident;
	else if(kind_tbl[tkn->text[0]] == Digit) tkn->kind = Num;
}

void err_exit(char* cause) {
	printf("Error at row: %d, col: %d: %s\n", row, col, cause);
	exit(1);
}

char* kind_to_str(tkn_kind kind) {
	return
		kind == Digit		? "Digit"		:
		kind == Letter		? "Letter"		:
		kind == Char		? "Char"		:
		kind == Ident		? "Ident"		:
		kind == String		? "String"		:
		kind == Num			? "Num"			:
		kind == Lparen		? "Lparen"		:
		kind == Rparen		? "Rparen"		:
		kind == Lbrace		? "Lbrace"		:
		kind == Rbrace		? "Rbrace"		:
		kind == Lbracket	? "Lbracket"	:
		kind == Rbracket	? "Rbracket"	:
		kind == Less		? "Less"		:
		kind == LessEq		? "LessEq"		:
		kind == Great		? "Great"		:
		kind == GreatEq		? "GreatEq"		:
		kind == Equal		? "Equal"		:
		kind == NotEq		? "NotEq"		:
		kind == Not			? "Not"			:
		kind == Plus		? "Plus"		:
		kind == Minus		? "Minus"		:
		kind == Multi		? "Multi"		:
		kind == Div			? "Div"			:
		kind == Mod			? "Mod"			:
		kind == And			? "And"			:
		kind == Or			? "Or"			:
		kind == Colon		? "Colon"		:
		kind == Semicolon	? "Semicolon"	:
		kind == Question	? "Question"	:
		kind == Inc			? "Inc"			:
		kind == Dec			? "Dec"			:
		kind == If			? "If"			:
		kind == Else		? "Else"		:
		kind == While		? "While"		:
		kind == For			? "For"			:
		kind == SngCmnt		? "SngCmnt"		:
		kind == MltCmnt		? "MltCmnt"		:
		kind == Assign		? "Assign"		:
		kind == Sharp		? "Sharp"		:
		kind == Yen			? "Yen"			:
		kind == Comma		? "Comma"		:
		kind == Dot			? "Dot"			:
		kind == SngQ		? "SngQ"		:
		kind == DblQ		? "DblQ"		:
		kind == AtSign		? "AtSign"		:
		kind == Amp			? "Amp"			:
		kind == Pipe		? "Pipe"		: "Other";
}

