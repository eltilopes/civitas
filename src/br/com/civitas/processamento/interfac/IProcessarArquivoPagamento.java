package br.com.civitas.processamento.interfac;

import java.util.List;

import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.ResumoSetor;

public interface IProcessarArquivoPagamento {

	List<ResumoSetor> processar(ArquivoPagamento arquivo) throws Exception ;
	 
}
