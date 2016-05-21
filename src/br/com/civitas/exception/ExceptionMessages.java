package br.com.civitas.exception;

/**
 * 
 * @author Stanley Albuquerque
 *
 */

public class ExceptionMessages {
	
	public static final String CAPACIDADE_LOGICA_ATINGIDA  = "O número de aluno(s) ultrapassa o limite máximo da capacidade lógica desta classe.";
	public static final String ENTURMACAO_MENOS_HUM_ALUNO  = "A classe deverá conter pelo menos 01(hum) aluno.";
	public static final String ENTURMACAO_PROFESSOR_LOTADO = "Não poderá ser retirado alunos da classe. Devido professor lotado e/ou turma com menos alunos do que o permitido.";
	public static final String CLASSE_NAO_SELECIONADA = "A Nova Classe deverá ser selecionada.";
	public static final String ENTURMACAO_JA_EXISTENTE = "Aluno já matriculado na rede de ensino.";
	public static final String JA_NO_REGISTRO_UNICO = "Aluno já cadastrado no Registro Único.";
	public static final String ALUNO_DUPLICADO = "Aluno duplicado.";
	public static final String ENTURMACAO_MENOS_HUM_ALUNO_CONTRA_TURNO  = "Não é possível desenturmar todos os alunos desta turma devido a mesma"
																		+ " possuir professor lotado. A turma deverá conter pelo menos 01 (um) aluno.";
	
}

