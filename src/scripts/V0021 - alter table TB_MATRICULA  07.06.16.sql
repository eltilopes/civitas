delete from tb_evento_pagamento;
delete from tb_pagamento;
delete from tb_matricula;
delete from tb_arquivo_pagamento;
delete from tb_evento;
delete from tb_cargo;
delete from tb_vinculo;
delete from tb_unidade_trabalho;
delete from tb_secretaria;
delete from tb_setor;
delete from tb_nivel_pagamento;


alter table TB_MATRICULA ALTER cd_setor  drop NOT NULL;
