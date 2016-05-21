package br.com.civitas.exception;

public class EnturmacaoException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public EnturmacaoException(String message) {
		super(message);
	}
	
	public EnturmacaoException() {
		super(ExceptionMessages.ENTURMACAO_PROFESSOR_LOTADO);
	}

}
