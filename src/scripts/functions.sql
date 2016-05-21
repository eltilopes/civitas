create or replace function fc_encontra_aluno_duplicado(nome_aluno in varchar2, nome_mae in varchar2, dt_nasc in varchar2)
return educacao.table_of_aluno
is
  alunos educacao.table_of_aluno;
  v_hash varchar2(200);
  v_dt_nasc date;
begin
  
  v_dt_nasc := to_date(dt_nasc, 'dd/mm/yyyy');
  alunos := educacao.table_of_aluno();
  v_hash := util.fc_retira_espacos(
               util.fc_retira_acento_numero(
                   util.fc_retira_espacos(nome_aluno||nome_mae)
               )
            )||v_dt_nasc;
  
  for alu in 
    (select a.* from educacao.tb_aluno a where a.hash_aluno = util.buscabr_frase(v_hash)) loop
     alunos.extend();
     alunos(alunos.count) := educacao.aluno(alu.ci_aluno, alu.nm_aluno, alu.nm_mae, alu.dt_nascimento, 1);
                     
  end loop;
  
  return alunos;
end;

--------------------

create or replace function util.fc_retira_espacos(str in varchar2) return varchar2 is
  v_retorno varchar2(2000);
begin
  for reg in (select regexp_substr(trim(str),'[^ ]+', 1, level) campos
                from dual
              connect by regexp_substr(trim(str),'[^ ]+', 1, level) is not null)
  loop
    v_retorno := v_retorno ||reg.campos;
  end loop;

  return v_retorno;
end;

-------------------

create or replace function util.fc_retira_acento_numero(str in varchar2) return varchar is
  semacento varchar2(2000);
begin
  semacento:= upper(str);
  semacento:= translate(semacento, '¡…Õ”⁄¿»Ã“Ÿ√’¬ Œ‘€‹«™∫∞0123456789', 'AEIOUAEIOUAOAEIOUUC ');
  return semacento;
end;

-------------------

create or replace function util.fc_retira_espacos(str in varchar2) return varchar2 is
  v_retorno varchar2(2000);
begin
  for reg in (select regexp_substr(trim(str),'[^ ]+', 1, level) campos
                from dual
              connect by regexp_substr(trim(str),'[^ ]+', 1, level) is not null)
  loop
    v_retorno := v_retorno ||reg.campos;
  end loop;

  return v_retorno;
end;

--------------------

CREATE OR REPLACE TYPE table_of_aluno IS TABLE OF educacao.aluno;

