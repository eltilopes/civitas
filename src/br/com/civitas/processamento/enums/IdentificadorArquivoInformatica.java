package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoInformatica {

		INICIO_EVENTO("VENCIMENTO BASE"),
		FIM_EVENTO("VANTAGENS..:"), 
		INICIO_RESUMO_SETOR("RESUMO DO SETOR:"),
		FIM_RESUMO_SETOR("BASE PREV. MUNICIPAL:"), 
		INICIO_RESUMO_GERAL("RESUMO GERAL"),
		INICIO_RESUMO_SECRETARIA("RESUMO DA SECRETARIA"),
		FIM_RESUMO_GERAL("SISTEMA DE RECURSOS HUMANOS E FOLHA DE PAGAMENTO"), 
		SECRETARIA("SEC.:"), 
		SETOR("SETOR:"), 
		VENCIMENTO_BASE("VENCIMENTO BASE"), 
		VANTAGEM("VANTAGENS..:"), 
		DESCONTO("DESCONTOS..:"), 
		ESPACO_NA_LINHA(" ")	,	
		BARRA("/")	,	
		ATS("ATS")	,	
		CONSIGNACAO_CEF("CONSIGNACAO CEF")	,	
		PERCENTUAL("%")	,	
		VIRGULA(","), 
		INICIO_PAGAMENTOS("COD. SERVIDOR"), 
		FIM_PAGAMENTO_INDIVIDUAL("VALOR LÍQUIDO..:"), 
		SEPARADOR_ARQUIVO("------------------------------------------------------------------------------------------------------------------------------------"), 
		IDENTIFICADOR_UNIDADE_TRABALHO("00000"), 
		TOTAL_SERVIDORES("TOTAL DE SERVIDORES:"), 
		TOTAL_DESCONTOS("DESCONTO:"), 
		TOTAL_PROVENTOS("VANTAGEM:"), 
		TOTAL_LIQUIDO("LÍQUIDO.:");
	
		private String descricao;
		
		private IdentificadorArquivoInformatica(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
