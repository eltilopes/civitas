package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoTarget {

		INICIO_EVENTO("Referência Proventos Descontos Valor Líquido"),
		FIM_EVENTO("CPF:"), 
		INICIO_RESUMO_SETOR("RESUMO DO SETOR"),
		FIM_RESUMO_SETOR("Total de Vínculos:"), 
		ORGAO("Órgão:"), 
		COMPETENCIA("COMPETENCIA....:"), 
		EMISSAO("Emissão:"), 
		REFERENCIA("REFERENCIA.....:"), 
		PAGAMENTO_BANCO("Pagamento em Banco"), 
		TOTAL_ORCAMENTARIO("Total Orçamentário"), 
		DEPENDENTES("DEPENDENTES:"), 
		SETOR("SETOR:"), 
		CPF("CPF:"), 
		TOTAIS("Totais"), 
		LOTACAO("Lotação.:"), 
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
		VIRGULA(","), 
		VINCULO("Vínc"), 
		INICIO_PAGAMENTOS("Vínc"), 
		FIM_PAGAMENTOS("CPF:"), 
		FUNDO_RESERVA("Fundo de Reserva"), 
		FIM_PAGAMENTO_INDIVIDUAL("VALOR LÍQUIDO..:"), 
		TIPO("Tipo:"); 
	
		private String descricao;
		
		private IdentificadorArquivoTarget(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
