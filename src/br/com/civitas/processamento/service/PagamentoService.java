package br.com.civitas.processamento.service;

import java.util.List;
import java.util.Objects;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;

@Service
public class PagamentoService extends AbstractPersistence<Pagamento> {

	private static final long serialVersionUID = 670233112671915705L;

	@Autowired
	private MatriculaService matriculaService;
	
	@Autowired
	private EventoPagamentoService eventoPagamentoService;
	
	@Autowired
	private ArquivoPagamentoService arquivoService;
	
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
	
	@SuppressWarnings("unchecked")
	public List<Pagamento> getPagamentoPorArquivo(ArquivoPagamento arquivoPagamento, Cargo cargo, Secretaria secretaria, Setor setor) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p FROM Pagamento p ");
		sql.append("INNER JOIN FETCH p.arquivo ap ");
		sql.append("INNER JOIN FETCH p.matricula m ");
		sql.append("INNER JOIN FETCH m.cargo ca ");
		sql.append("INNER JOIN FETCH m.secretaria sec ");
		sql.append("INNER JOIN FETCH m.setor setor ");
		sql.append("INNER JOIN FETCH m.vinculo v ");
		sql.append("INNER JOIN FETCH ap.cidade c ");
		sql.append("INNER JOIN FETCH ap.mes m ");
		sql.append("INNER JOIN FETCH ap.ano a ");
		sql.append("WHERE 1 = 1 ");
		sql.append(Objects.nonNull(arquivoPagamento.getCidade()) ? "AND c = :cidade " : "");
		sql.append(Objects.nonNull(arquivoPagamento.getMes()) ? "AND m = :mes " : "");
		sql.append(Objects.nonNull(arquivoPagamento.getAno()) ? "AND a = :ano " : "");
		sql.append(Objects.nonNull(cargo) ? "AND ca = :cargo " : "");
		sql.append(Objects.nonNull(secretaria) ? "AND sec = :secretaria " : "");
		sql.append(Objects.nonNull(setor) ? "AND setor = :setor " : "");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		if(Objects.nonNull(arquivoPagamento.getCidade())){
			query.setParameter("cidade", arquivoPagamento.getCidade());
		}
		if(Objects.nonNull(arquivoPagamento.getMes())){
			query.setParameter("mes", arquivoPagamento.getMes());
		}
		if(Objects.nonNull(arquivoPagamento.getAno())){
			query.setParameter("ano", arquivoPagamento.getAno());
		}
		if(Objects.nonNull(cargo)){
			query.setParameter("cargo", cargo);
		}
		if(Objects.nonNull(secretaria)){
			query.setParameter("secretaria", secretaria);
		}
		if(Objects.nonNull(setor)){
			query.setParameter("setor", setor);
		}
		return query.list();
	}
	
	@Transactional
	public void inserirPagamentos(List<Pagamento> pagamentos, List<Evento> eventos, ArquivoPagamento arquivo)  {
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
	
	private ArquivoPagamento setResumoArquivo(List<Pagamento> pagamentos, List<Evento> eventos, ArquivoPagamento arquivo) {
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
