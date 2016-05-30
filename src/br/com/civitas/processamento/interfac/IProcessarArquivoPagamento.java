package br.com.civitas.processamento.interfac;

import br.com.civitas.processamento.entity.ArquivoPagamento;

public interface IProcessarArquivoPagamento {

	 void processar(ArquivoPagamento arquivo) throws Exception ;
	 
}
