package br.com.civitas.processamento.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Pagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;

@Service
public class PagamentoService extends AbstractPersistence<Pagamento> {

	private static final long serialVersionUID = 670233112671915705L;

	@Autowired
	private EventoPagamentoService eventoPagamentoService;
	
	@Autowired
	private ArquivoPagamentoService arquivoService;
	
	@Autowired
	SqlSession sqlSession;
	
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
	
	public List<Pagamento> getPagamentoPorArquivo(ArquivoPagamento arquivoPagamento, List<Cargo> cargosSelecionados, List<Secretaria> secretarias, 
			List<Setor> setoresSelecionados, List<UnidadeTrabalho> unidadesSelecionadas, List<NivelPagamento> niveisSelecionados, List<CargaHorariaPagamento> cargasSelecionados) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("secretarias", convertListToString(secretarias));
		parameters.put("setoresSelecionados", convertListToString(setoresSelecionados));
		parameters.put("cargosSelecionados", convertListToString(cargosSelecionados));
		parameters.put("cargasSelecionados", convertListToString(cargasSelecionados));
		parameters.put("niveisSelecionados", convertListToString(niveisSelecionados));
		parameters.put("unidadesSelecionadas", convertListToString(unidadesSelecionadas));
		parameters.put("idCidade", Objects.nonNull(arquivoPagamento.getCidade()) ? arquivoPagamento.getCidade().getId() : null);
		parameters.put("idAno", Objects.nonNull(arquivoPagamento.getAno()) ? arquivoPagamento.getAno().getId() : null);
		parameters.put("idMes", Objects.nonNull(arquivoPagamento.getMes()) ? arquivoPagamento.getMes().getId() : null);
		return sqlSession.selectList("getPagamentoPorArquivo",parameters);
	}
	
	@Transactional
	public void inserirPagamentos(List<Pagamento> pagamentos, List<Evento> eventos, ArquivoPagamento arquivo)  {
		try {
			arquivo = arquivoService.save(setResumoArquivo(pagamentos,eventos, arquivo));
			for(Pagamento p : pagamentos){
				p.setArquivo(arquivo);
				p = save(p);
				eventoPagamentoService.inserirEventosPagamento(p.getEventosPagamento());
			}
		} catch (Exception e) {
			e.printStackTrace();
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
