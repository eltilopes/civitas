package br.com.civitas.processamento.enums;

public enum TipoArquivo {

		NENHUM("NENHUM ARQUIVO","NENHUM ARQUIVO", 0, 0), 
		ARQUIVO_LAYOUT("ARQUIVO LAYOUT","<< AGRUPAMENTO:", 1, 5),
		ARQUIVO_INFORMATICA("S&S INFORMATICA","RELATORIO DA FOLHA DE PAGAMENTO", 2, 3),
		ARQUIVO_TARGET("ARQUIVO TARGET","COMPETENCIA", 3, 8),
		ARQUIVO_DIGIMAX("ARQUIVO DIGIMAX","MAXFOLHA", 4, 5);
	
		private String descricao;
		private String chaveValidacao;
		private int codigo;
		private int fimProcessamento;
		
		private TipoArquivo(String descricao,String chaveValidacao, int codigo, int fimProcessamento){
			this.chaveValidacao = chaveValidacao;
			this.descricao  = descricao;
			this.codigo = codigo;
			this.fimProcessamento = fimProcessamento;
		}
		
		public String getDescricao(){
			return descricao;
		}

		public int getCodigo(){
			return codigo;
		}

		public String getChaveValidacao() {
			return chaveValidacao;
		}

		public int getFimProcessamento() {
			return fimProcessamento;
		}

	}
