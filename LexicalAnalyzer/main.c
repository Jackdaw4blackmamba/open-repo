//以下の書籍を大いに参考にしています
//林晴比古, 「明快入門 コンパイラインタプリタ開発」, ソフトバンク クリエイティブ株式会社, 第1刷.
#include <stdio.h>
#include <stdlib.h>
#include "token_prots.h"

void main(int argc, char* argv[]) {
	if(argc < 2){
		puts("Error: missing file name");
		exit(1);
	}

	char *filename = argv[1];
	token t;

	load_from_file(filename);
	init();

	t = next_token();
	while(t.kind != NulKind) {
		printf("%-25s: %s\n", t.text, kind_to_str(t.kind));
		t = next_token();
	}
}
