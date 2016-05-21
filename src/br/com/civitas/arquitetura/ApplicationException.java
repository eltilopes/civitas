package br.com.civitas.arquitetura;

public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = -4570323261569410812L;

	public ApplicationException( String message ){
		super( message );
	}
	
	public ApplicationException( String message, Throwable cause ){
		super( message,cause );
	}
}