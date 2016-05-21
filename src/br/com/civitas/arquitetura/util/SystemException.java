package br.com.civitas.arquitetura.util;

public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 6825194309280622126L;

	public SystemException() {
		super();
	}

	public SystemException(final String message) {
		super(message);
	}

	public SystemException(final Throwable cause) {
		super(cause);
	}

	public SystemException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
