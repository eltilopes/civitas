alter table tb_cidade add column fl_ativa boolean;

update tb_cidade set fl_ativa = false;

ALTER TABLE tb_cidade ALTER COLUMN fl_ativa SET NOT NULL;