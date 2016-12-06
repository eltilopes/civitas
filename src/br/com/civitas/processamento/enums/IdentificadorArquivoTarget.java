package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoTarget {

		INICIO_EVENTO("Refer�ncia Proventos Descontos Valor L�quido"),
		FIM_EVENTO("CPF:"), 
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
		CARGO("Cargo 2"), 
		CPF("CPF:"), 
		TOTAIS("Totais"), 
		ANUENIO("ANUENIO"), 
		LOTACAO("Lota��o.:"), 
		ADMISSAO("Dta. Admiss.:"), 
		NIVEL_PAGAMENTO("Nivel Salarial:"), 
		CARGA_HORARIA("CH:"), 
		VANTAGEM("VANTAGENS..:"), 
		DESCONTO("DESCONTOS..:"), 
		ESPACO_NA_LINHA(" ")	,	
		PIS_PASEP("Pis/Pasep:")	,	
		BARRA("/")	,	
		PERCENTUAL("%")	,	
		HIFEN("-")	,	
		SALARIO_BASE("SALARIO BASE")	,	
		VENCIMENTO_BASE("VENCIMENTO BASE")	,	
		LICENCA_MATERNIDADE("LICENCA MATERNIDADE")	,	
		VENCIMENTO_BASE_LEI("VENCIMENTO BASE LEI 2.286/15")	,	
		VIRGULA(","), 
		VINCULO("V�nc"), 
		INICIO_PAGAMENTOS("V�nc"), 
		FIM_PAGAMENTOS("CPF:"), 
		FUNDO_RESERVA("Fundo de Reserva"), 
		FIM_PAGAMENTO_INDIVIDUAL("VALOR L�QUIDO..:"), 
		TIPO("Tipo:"), 
		TOTAL_DESCONTOS("Total de Descontos"), 
		TOTAL_PROVENTOS("Total de Proventos"), 
		TOTAL_LIQUIDO("Total L�quido"), 
		PREFEITURA_MUNICIPAL("PREFEITURA MUNICIPAL"); 
	
		private String descricao;
		
		private IdentificadorArquivoTarget(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
