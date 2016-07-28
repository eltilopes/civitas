package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoFolha {

		INICIO_EVENTO("VENCIMENTO"),
		FIM_EVENTO("----------------------------------------------------------------------------------"), 
		INICIO_RESUMO_SETOR("RESUMO DA FOLHA"),
		FIM_RESUMO_SETOR("TOTAL DE SERVIDORES"), 
		DEPARTAMENTO("DEPARTAMENTO:"), 
		COMPETENCIA("COMPETENCIA....:"), 
		FOLHA("FOLHA.......:"), 
		REFERENCIA("REFERENCIA.....:"), 
		DEPENDENTES("DEPENDENTES:"), 
		SETOR("SETOR:"), 
		CPF("CPF:"), 
		ADMISSAO("ADMISSAO:"), 
		CARGA_HORARIA("CARGA HORARIA:"), 
		VANTAGEM("VANTAGENS..:"), 
		DESCONTO("DESCONTOS..:"), 
		ESPACO_NA_LINHA(" ")	,	
		BARRA("/")	,	
		PERCENTUAL("%")	,	
		HIFEN("-")	,	
		VIRGULA(","), 
		INICIO_PAGAMENTOS("SERVIDOR  PROVENTOS                                              VALOR    DESCONTOS                                                  VALOR              LIQUIDO"), 
		FIM_PAGAMENTOS("PAGINA"), 
		FIM_PAGAMENTO_INDIVIDUAL("VALOR LÍQUIDO..:"), 
		SEPARADOR_ARQUIVO("-------------------------------------------------------------------------------------------------------"); 
	
		private String descricao;
		
		private IdentificadorArquivoFolha(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
