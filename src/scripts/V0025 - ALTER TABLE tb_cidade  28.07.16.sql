select * from tb_cidade c where c.ds_descricao  = 'AMA';
delete from tb_cidade c where c.ds_descricao  = 'AMA';
update tb_cidade set ds_descricao  = upper(ds_descricao);

ALTER TABLE tb_cidade ADD CONSTRAINT uk_descricao_estado_key UNIQUE (ds_descricao, cd_estado);

