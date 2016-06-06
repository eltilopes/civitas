CREATE TABLE tb_unidade_trabalho
(
  ci_unidade_trabalho bigint NOT NULL,
  ds_descricao character varying NOT NULL,
  cd_cidade bigint NOT NULL,
  nr_tipo_arquivo integer NOT NULL,
  nr_codigo integer NOT NULL,
  CONSTRAINT pk_unidade_trabalho PRIMARY KEY (ci_unidade_trabalho),
  CONSTRAINT tb_unidade_trabalho_cd_cidade_fkey FOREIGN KEY (cd_cidade)
      REFERENCES tb_cidade (ci_cidade) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tb_unidade_descricao_codigo_key UNIQUE (ds_descricao, cd_cidade, nr_tipo_arquivo,nr_codigo)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_unidade_trabalho
  OWNER TO postgres;
  
CREATE SEQUENCE seq_unidade_trabalho
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_unidade_trabalho
  OWNER TO postgres;

ALTER TABLE TB_MATRICULA ADD COLUMN cd_unidade_trabalho bigint ;
ALTER TABLE tb_matricula ADD FOREIGN KEY (cd_unidade_trabalho) REFERENCES tb_unidade_trabalho;