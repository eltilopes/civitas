CREATE TABLE tb_carga_horaria_pagamento
(
  ci_carga_horaria_pagamento bigint NOT NULL,
  ds_descricao character varying NOT NULL,
  cd_cidade bigint NOT NULL,
  nr_tipo_arquivo integer NOT NULL,
  ds_codigo character varying  NOT NULL,
  CONSTRAINT pk_carga_horaria_pagamento PRIMARY KEY (ci_carga_horaria_pagamento),
  CONSTRAINT fk_carga_cidade_fkey FOREIGN KEY (cd_cidade)
      REFERENCES tb_cidade (ci_cidade) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tb_carga_descricao_codigo_key UNIQUE (ds_descricao, cd_cidade, nr_tipo_arquivo,ds_codigo)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_carga_horaria_pagamento
  OWNER TO postgres;
  
CREATE SEQUENCE seq_carga_horaria_pagamento
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_carga_horaria_pagamento
  OWNER TO postgres;

ALTER TABLE TB_MATRICULA ADD COLUMN cd_carga_horaria_pagamento bigint ;
ALTER TABLE tb_matricula ADD FOREIGN KEY (cd_carga_horaria_pagamento) REFERENCES tb_carga_horaria_pagamento;


