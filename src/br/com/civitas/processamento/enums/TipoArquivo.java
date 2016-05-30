package br.com.civitas.processamento.enums;

public enum TipoArquivo {

		NENHUM("NENHUM ARQUIVO", 0), 
		ARQUIVO_LAYOUT("ARQUIVO LAYOUT", 1),
		FOLHA_PAGAMENTO("FOLHA PAGAMENTO", 2);
	
		private String descricao;
		private int codigo;
		
		private TipoArquivo(String descricao, int codigo){
			this.descricao  = descricao;
			this.codigo = codigo;
		}
		
		public String getDescricao(){
			return descricao;
		}

		public int getCodigo(){
			return codigo;
		}
		
	}
