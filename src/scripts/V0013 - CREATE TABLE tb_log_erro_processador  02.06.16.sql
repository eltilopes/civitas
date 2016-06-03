drop table tb_log_erro_processador;
drop sequence seq_log_erro_processador;

CREATE TABLE tb_log_erro_processador
(
  ci_log_erro_processador integer NOT NULL,
  dt_data timestamp with time zone NOT NULL,
  ds_erro character varying NOT NULL,
  nm_arquivo character varying NOT NULL,
  CONSTRAINT pk_log_erro_processador PRIMARY KEY (ci_log_erro_processador)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_log_erro_processador
  OWNER TO postgres;


CREATE SEQUENCE seq_log_erro_processador
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_log_erro_processador
  OWNER TO postgres;