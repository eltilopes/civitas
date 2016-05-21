package br.com.civitas.exception;

/**
 * 
 * @author Stanley Albuquerque
 *
 */

public class ExceptionMessages {
	
	public static final String CAPACIDADE_LOGICA_ATINGIDA  = "O n�mero de aluno(s) ultrapassa o limite m�ximo da capacidade l�gica desta classe.";
	public static final String ENTURMACAO_MENOS_HUM_ALUNO  = "A classe dever� conter pelo menos 01(hum) aluno.";
	public static final String ENTURMACAO_PROFESSOR_LOTADO = "N�o poder� ser retirado alunos da classe. Devido professor lotado e/ou turma com menos alunos do que o permitido.";
	public static final String CLASSE_NAO_SELECIONADA = "A Nova Classe dever� ser selecionada.";
	public static final String ENTURMACAO_JA_EXISTENTE = "Aluno j� matriculado na rede de ensino.";
	public static final String JA_NO_REGISTRO_UNICO = "Aluno j� cadastrado no Registro �nico.";
	public static final String ALUNO_DUPLICADO = "Aluno duplicado.";
	public static final String ENTURMACAO_MENOS_HUM_ALUNO_CONTRA_TURNO  = "N�o � poss�vel desenturmar todos os alunos desta turma devido a mesma"
																		+ " possuir professor lotado. A turma dever� conter pelo menos 01 (um) aluno.";
	
}

