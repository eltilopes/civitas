CREATE TABLE tb_setor
(
  ci_setor bigint NOT NULL,
  ds_descricao character varying NOT NULL,
  cd_cidade bigint NOT NULL,
  nr_tipo_arquivo integer NOT NULL,
  CONSTRAINT pk_setor PRIMARY KEY (ci_setor),
  CONSTRAINT tb_setor_cd_cidade_fkey FOREIGN KEY (cd_cidade)
      REFERENCES tb_cidade (ci_cidade) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tb_setor_ds_descricao_key UNIQUE (ds_descricao, cd_cidade, nr_tipo_arquivo)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_setor
  OWNER TO postgres;
  
CREATE SEQUENCE seq_setor
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_setor
  OWNER TO postgres;

ALTER TABLE TB_MATRICULA ADD COLUMN cd_setor bigint not null;
ALTER TABLE tb_matricula ADD FOREIGN KEY (cd_setor) REFERENCES tb_setor;