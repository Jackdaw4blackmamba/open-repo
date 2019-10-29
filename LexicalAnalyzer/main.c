//�ȉ��̏��Ђ�傢�ɎQ�l�ɂ��Ă��܂�
//�ѐ����, �u�������� �R���p�C���C���^�v���^�J���v, �\�t�g�o���N �N���G�C�e�B�u�������, ��1��.
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
