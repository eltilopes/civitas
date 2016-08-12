package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoTarget {

		INICIO_EVENTO("Refer�ncia Proventos Descontos Valor L�quido"),
		FIM_EVENTO("CPF"), 
		INICIO_RESUMO_SETOR("RESUMO DO SETOR"),
		FIM_RESUMO_SETOR("Total de V�nculos:"), 
		ORGAO("�rg�o:"), 
		COMPETENCIA("COMPETENCIA....:"), 
		EMMISSAO("Emiss�o:"), 
		REFERENCIA("REFERENCIA.....:"), 
		DEPENDENTES("DEPENDENTES:"), 
		SETOR("SETOR:"), 
		CPF("CPF:"), 
		ADMISSAO("Dta. Admiss.:"), 
		CARGA_HORARIA("CARGA HORARIA:"), 
		VANTAGEM("VANTAGENS..:"), 
		DESCONTO("DESCONTOS..:"), 
		ESPACO_NA_LINHA(" ")	,	
		BARRA("/")	,	
		PERCENTUAL("%")	,	
		HIFEN("-")	,	
		VIRGULA(","), 
		VINCULO("V�nc"), 
		INICIO_PAGAMENTOS("V�nc"), 
		FIM_PAGAMENTOS("CPF:"), 
		FIM_PAGAMENTO_INDIVIDUAL("VALOR L�QUIDO..:"), 
		TIPO("Tipo:"); 
	
		private String descricao;
		
		private IdentificadorArquivoTarget(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
