package br.com.civitas.processamento.service;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.EventoPagamento;

@Service
public class EventoPagamentoService extends AbstractPersistence<EventoPagamento> {

	private static final long serialVersionUID = -6298894111816814631L;

	@Override
	protected Class<EventoPagamento> getClazz() {
		return EventoPagamento.class;
	}

	@SuppressWarnings("unchecked")
	public List<EventoPagamento> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ep FROM EventoPagamento ep");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<EventoPagamento>) query.list();
	}

	@Transactional
	public void inserirEventosPagamento(List<EventoPagamento> eventosPagamento) {
		for(EventoPagamento ep : eventosPagamento){
			ep = save(ep);
		}
	}
	
}
