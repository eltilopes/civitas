package br.com.civitas.processamento.interfac;

import java.util.List;

import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Pagamento;

public interface IProcessarArquivoPagamento {

	 List<Pagamento> getPagamentos(ArquivoPagamento arquivo) throws Exception ;
	 
}
