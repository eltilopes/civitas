package br.com.civitas.processamento.enums;

public enum TipoArquivo {

		NENHUM("NENHUM ARQUIVO","NENHUM ARQUIVO", 0), 
		ARQUIVO_LAYOUT("ARQUIVO LAYOUT","<< AGRUPAMENTO:", 1),
		FOLHA_PAGAMENTO("FOLHA PAGAMENTO","FOLHA PAGAMENTO", 2);
	
		private String descricao;
		private String chaveValidacao;
		private int codigo;
		
		private TipoArquivo(String descricao,String chaveValidacao, int codigo){
			this.chaveValidacao = chaveValidacao;
			this.descricao  = descricao;
			this.codigo = codigo;
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
		
	}
