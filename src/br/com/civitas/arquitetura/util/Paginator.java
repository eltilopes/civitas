package br.com.civitas.arquitetura.util;

public abstract class Paginator {

	private int paginaClicada = 1;

	private PagedDataModel dataModel;
	
	public void reset(){
		paginaClicada = 1;
	}
	
	public void resetAll(){
		reset();
		dataModel = null;
	}
	
	public int getFirst(){
		if(dataModel == null){
			return 0;
		}
		return paginaClicada * dataModel.getPageSize() - dataModel.getPageSize();
	}

	public abstract void pesquisar();

	public PagedDataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(PagedDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getPaginaClicada() {
		return paginaClicada;
	}

	public void setPaginaClicada(Integer paginaClicada) {
		this.paginaClicada = paginaClicada;
	}

}
