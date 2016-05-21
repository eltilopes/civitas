package br.com.civitas.enums;

public enum Transacoes {
	
	CADASTRO_GERENTE("cadastroGerente"),
	CADASTRO_EXCEDENTE("cadastroExcedente"),
	CAPACIDADE_LOGICA_MAIOR_QUE_FISICA("capacidadeLogicaMaiorQueFisica");
	
	private String value;
	
	private Transacoes(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}

}
