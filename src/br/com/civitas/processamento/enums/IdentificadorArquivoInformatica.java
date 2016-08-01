package br.com.civitas.processamento.enums;

public enum IdentificadorArquivoInformatica {

		INICIO_EVENTO("VENCIMENTO BASE"),
		FIM_EVENTO("VANTAGENS..:"), 
		INICIO_RESUMO_SETOR("RESUMO DO SETOR:"),
		FIM_RESUMO_SETOR("TOTAL DE SERVIDORES:"), 
		INICIO_RESUMO_GERAL("RESUMO GERAL"),
		FIM_RESUMO_GERAL("SISTEMA DE RECURSOS HUMANOS E FOLHA DE PAGAMENTO"), 
		SECRETARIA("SEC.:"), 
		SETOR("SETOR:"), 
		VENCIMENTO_BASE("VENCIMENTO BASE"), 
		VANTAGEM("VANTAGENS..:"), 
		DESCONTO("DESCONTOS..:"), 
		ESPACO_NA_LINHA(" ")	,	
		BARRA("/")	,	
		PERCENTUAL("%")	,	
		VIRGULA(","), 
		INICIO_PAGAMENTOS("COD. SERVIDOR/CARGO FUNÇÃO/LOTAÇÃO NATUREZA/CC  ADMISSÃO"), 
		FIM_PAGAMENTO_INDIVIDUAL("VALOR LÍQUIDO..:"), 
		SEPARADOR_ARQUIVO("------------------------------------------------------------------------------------------------------------------------------------"), 
		IDENTIFICADOR_UNIDADE_TRABALHO("00000");
	
		private String descricao;
		
		private IdentificadorArquivoInformatica(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
