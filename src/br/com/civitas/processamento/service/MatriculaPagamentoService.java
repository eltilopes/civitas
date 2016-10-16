package br.com.civitas.processamento.service;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.MatriculaPagamento;

@Service
public class MatriculaPagamentoService extends AbstractPersistence<MatriculaPagamento> {

	private static final long serialVersionUID = -5829554267277214171L;

	@Override
	protected Class<MatriculaPagamento> getClazz() {
		return MatriculaPagamento.class;
	}
	
	public MatriculaPagamento salvar(MatriculaPagamento matriculaPagamento){
		getSession().merge(matriculaPagamento.getVinculo());
		getSession().merge(matriculaPagamento.getCargo());
		return save(matriculaPagamento);
	}

}
