insert into tb_modulo (CI_MODULO, DS_MODULO, DOMINIO, PORTA, NM_SISTEMA)
values (175, 'SISTEMA DE GESTÃO EDUCACIONAL', 'sge.sme.fortaleza.ce.gov.br', ':8080', 'sge');

-- grupos --
insert into tb_grupo (CI_GRUPO, NM_GRUPO, CD_MODULO, DT_CADASTRO, FL_NIVEL_ACESSO)
values (seq_tb_grupo.nextval, 'MASTER', 175, '19/03/15 20:23:25,854000', 4);

insert into tb_grupo (CI_GRUPO, NM_GRUPO, CD_MODULO, DT_CADASTRO, FL_NIVEL_ACESSO)
values (seq_tb_grupo.nextval, 'ESCOLA', 175, '07/04/15 15:38:52,863000', 1);

-- trasações --
insert into tb_transacao (CI_TRANSACAO, NM_TRANSACAO, NM_LABEL)
values (util.seq_tb_transacao, 'ALUNO', 'alunos');

insert into tb_transacao (CI_TRANSACAO, NM_TRANSACAO, NM_LABEL)
values (util.seq_tb_transacao, 'SELEÇÃO DE ESCOLA', 'selecionarEscola');

insert into tb_transacao (CI_TRANSACAO, NM_TRANSACAO, NM_LABEL)
values (util.seq_tb_transacao, 'CADASTRO DE OCORRÊNCIA VIOLÊNCIA', 'violenciaOcorrencia');

insert into tb_transacao (CI_TRANSACAO, NM_TRANSACAO, NM_LABEL)
values (util.seq_tb_transacao, 'REGISTRO ÚNICO - PLANEJAMENTO', 'registroUnicoPlanejamento');

insert into tb_transacao (CI_TRANSACAO, NM_TRANSACAO, NM_LABEL)
values (util.seq_tb_transacao, 'REGISTRO UNICO', 'registroUnico');

-- modulotransação --
insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 1);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 2);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 3);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 4);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 484);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 485);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 486);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 492);

insert into tb_modulotransacao (CD_MODULO, CD_TRANSACAO)
values (175, 493);

-- grupotransações --
insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 1, 'S', 'S', 'S', '19/03/15 20:23:25,950000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 2, 'S', 'S', 'S', '19/03/15 20:23:25,963000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 3, 'S', 'S', 'S', '19/03/15 20:23:25,964000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 4, 'S', 'S', 'S', '19/03/15 20:23:25,966000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 335, 484, 'S', 'S', 'S', '07/04/15 15:39:04,622000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 335, 486, 'S', 'S', 'S', '07/04/15 15:39:05,893000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 335, 485, 'S', 'S', 'S', '07/04/15 15:39:07,449000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 484, 'S', 'S', 'S', '02/06/15 13:34:58,103000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 485, 'S', 'S', 'S', '02/06/15 13:34:58,848000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 486, 'S', 'S', 'S', '02/06/15 13:34:59,457000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 492, 'S', 'S', 'S', '02/06/15 13:34:59,849000', null);

insert into tb_grupotransacoes (CI_GRUPOTRANSACOES, CD_GRUPO, CD_TRANSACAO, FL_INSERIR, FL_ALTERAR, FL_DELETAR, DT_CRIACAO, CD_USUARIO_INSERT)
values (util.seq_tb_grupotransacoes, 332, 493, 'S', 'S', 'S', '02/06/15 14:14:18,254000', null);