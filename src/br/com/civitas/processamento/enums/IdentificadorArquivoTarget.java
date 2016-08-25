package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoTarget {

		INICIO_EVENTO("Refer�ncia Proventos Descontos Valor L�quido"),
		FIM_EVENTO("CPF"), 
		INICIO_RESUMO_SETOR("RESUMO DO SETOR"),
		FIM_RESUMO_SETOR("Total de V�nculos:"), 
		ORGAO("�rg�o:"), 
		COMPETENCIA("COMPETENCIA....:"), 
		EMISSAO("Emiss�o:"), 
		REFERENCIA("REFERENCIA.....:"), 
		PAGAMENTO_BANCO("Pagamento em Banco"), 
		TOTAL_ORCAMENTARIO("Total Or�ament�rio"), 
		DEPENDENTES("DEPENDENTES:"), 
		SETOR("SETOR:"), 
		CPF("CPF:"), 
		LOTACAO("Lota��o.:"), 
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
		FUNDO_RESERVA("Fundo de Reserva"), 
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
