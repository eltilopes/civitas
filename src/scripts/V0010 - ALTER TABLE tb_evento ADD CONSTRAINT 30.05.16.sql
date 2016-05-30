ALTER TABLE tb_evento ADD CONSTRAINT un_evento_cidade_chave_arquivo UNIQUE (nm_chave, nr_tipo_arquivo, cd_cidade);
