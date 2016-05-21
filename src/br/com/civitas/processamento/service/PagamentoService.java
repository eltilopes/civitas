package br.com.civitas.processamento.service;

import java.util.List;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Arquivo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.Pagamento;

@Service
public class PagamentoService extends AbstractPersistence<Pagamento> {

	private static final long serialVersionUID = 670233112671915705L;

	@Autowired
	private MatriculaService matriculaService;
	
	@Autowired
	private EventoPagamentoService eventoPagamentoService;
	
	@Autowired
	private ArquivoService arquivoService;
	
	@Override
	protected Class<Pagamento> getClazz() {
		return Pagamento.class;
	}

	@SuppressWarnings("unchecked")
	public List<Pagamento> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT p FROM Pagamento p");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<Pagamento>) query.list();
	}
	
	@Transactional
	public void inserirPagamentos(List<Pagamento> pagamentos, List<Evento> eventos, Arquivo arquivo)  {
		try {
			arquivo = arquivoService.save(setResumoArquivo(pagamentos,eventos, arquivo));
			for(Pagamento p : pagamentos){
				p.setMatricula(matriculaService.salvar(p.getMatricula()));
				p.setArquivo(arquivo);
				p = save(p);
				eventoPagamentoService.inserirEventosPagamento(p.getEventosPagamento());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private Arquivo setResumoArquivo(List<Pagamento> pagamentos, List<Evento> eventos, Arquivo arquivo) {
		for(Pagamento p : pagamentos){
			arquivo.setTotalDescontos(arquivo.getTotalDescontos() + p.getTotalDescontos());
			arquivo.setTotalLiquido(arquivo.getTotalLiquido() + p.getTotalLiquido());
			arquivo.setTotalProventos(arquivo.getTotalProventos() + p.getTotalProventos());
			arquivo.setTotalRemuneracao(arquivo.getTotalRemuneracao() + p.getTotalRemuneracao());
			arquivo.setTotalPagamentos(arquivo.getTotalPagamentos() + 1);
		}
		return arquivo;
	}

}
