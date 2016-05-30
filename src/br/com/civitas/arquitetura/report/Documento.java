package br.com.civitas.arquitetura.report;

import java.io.Serializable;
import java.text.MessageFormat;

public class Documento implements Serializable {
	
	private static final long serialVersionUID = 2315146279273186469L;

	private boolean popup;
	
	private String name;
	
	private Extensao extensao;
	
	private byte [] stream;
	
	public Documento() {}
	
	public Documento(byte [] stream) {
		this.popup    = true; 
		this.stream   = stream;
		this.extensao = Extensao.PDF;
	}
	
	public Documento(String name, Extensao extensao, byte [] stream, boolean popup) {
		super();
		this.popup = popup;
		this.name = name;
		this.extensao = extensao;
		this.stream = stream;
	}

	public boolean isPopup() {
		return popup;
	}

	public String getName() {
		return name;
	}

	public Extensao getExtensao() {
		return extensao;
	}
	
	public byte[] getStream() {
		return stream;
	}
	
	public void setPopup(boolean popup) {
		this.popup = popup;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExtensao(Extensao extensao) {
		this.extensao = extensao;
	}

	public void setStream(byte[] stream) {
		this.stream = stream;
	}
	
	public String getFormato() {
		
		if(extensao != null) {
			
			return extensao.name().toLowerCase();
		}
		
		return null;
	}
	
	public boolean isValido() {
		
		boolean valido = true;
		
		if(stream == null) {
			valido = false;
		}
		
		return valido;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0}.{1}", (name == null ? "documento" : name), extensao.name().toLowerCase());
	}
}