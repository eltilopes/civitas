ALTER TABLE tb_evento DROP CONSTRAINT un_evento_cidade_chave_arquivo ;  

ALTER TABLE tb_evento ADD CONSTRAINT un_evento_cidade_chave_arquivo UNIQUE (nm_chave, nm_nome, nr_tipo_arquivo, cd_cidade);