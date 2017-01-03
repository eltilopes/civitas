package br.com.civitas.processamento.enums;

public enum TipoEvento {

		NENHUM("NENHUM", 0), 
		DESCONTO("DESCONTO", 1),
		PROVENTO("PROVENTO", 2);
	
		private String descricao;
		private int codigo;
		
		private TipoEvento(String descricao, int codigo){
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
