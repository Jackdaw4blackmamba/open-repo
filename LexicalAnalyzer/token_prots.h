//以下の書籍を大いに参考にしています
//林晴比古, 「明快入門 コンパイラインタプリタ開発」, ソフトバンク クリエイティブ株式会社, 第1刷.
#ifndef TOKEN_PROTS_H_
#define TOKEN_PROTS_H_

#define TKN_TEXT_SIZ 64
#define KEY_TEXT_SIZ 16

typedef enum {
	Digit,		Letter,		Char,		Ident,		String,		Num,
	Lparen,		Rparen,		Lbrace,		Rbrace,		Lbracket,	Rbracket,
	Less,		LessEq,		Great,		GreatEq,	Equal,		NotEq,	Not,
	Plus,		Minus,		Multi,		Div,		Mod,
	And,		Or,
	Colon,		Semicolon,	Question,	Inc,		Dec,
	If,			Else,		While,		For,
	SngCmnt,	MltCmnt,
	Assign,		Sharp,		Yen,		Comma,		Dot,		SngQ,	DblQ,	AtSign,
	Amp,		Pipe,

	Other,		NulKind,	EndList
} tkn_kind;

typedef struct {
	tkn_kind kind;
	char text[TKN_TEXT_SIZ + 1];
} token;

typedef struct {
	char text[KEY_TEXT_SIZ + 1];
	tkn_kind kind;
} keyword;

void load_from_array(char[]);
void load_from_file(char*);
void init(void);
void init_kind_tbl(void);
int next_char(void);
token next_token(void);
token get_default_token(void);
int is_ope2(int, int);
void set_kind(token*);
void err_exit(char*);
char* kind_to_str(tkn_kind);

#endif
