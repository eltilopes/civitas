package br.com.civitas.processamento.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.arquitetura.report.Extensao;
import br.com.civitas.arquitetura.report.ReportService;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.ResumoSetor;
import br.com.civitas.processamento.factory.FactoryProcessarArquivoPagamento;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ArquivoPagamentoService extends AbstractPersistence<ArquivoPagamento> {

	private static final long serialVersionUID = -7300710483142299659L;
	
	private IProcessarArquivoPagamento processarArquivoPagamento;
	
	@Autowired
	private FactoryProcessarArquivoPagamento factoryProcessarArquivoPagamento;
	
	@Autowired 
	private ReportService reportService;
	
	@Override
	protected Class<ArquivoPagamento> getClazz() {
		return ArquivoPagamento.class;
	}

	public List<ResumoSetor> processarArquivo(ArquivoPagamento arquivoPagamento) throws Exception {
		processarArquivoPagamento = factoryProcessarArquivoPagamento.getInstanciaProcessamento(arquivoPagamento.getTipoArquivo());
		return processarArquivoPagamento.processar(arquivoPagamento);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<ArquivoPagamento> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a FROM ArquivoPagamento a");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<ArquivoPagamento>) query.list();
	}

	public ArquivoPagamento buscarPorArquivo(ArquivoPagamento arquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a FROM ArquivoPagamento a ");
		sql.append(" WHERE a.ano = :ano ");
		sql.append(" AND a.mes = :mes  ");
		sql.append(" AND a.cidade = :cidade  ");
		sql.append(" AND a.tipoArquivo = :tipoArquivo  ");
		return (ArquivoPagamento) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("ano", arquivo.getAno())
									  .setParameter("mes", arquivo.getMes())
									  .setParameter("cidade", arquivo.getCidade())
									  .setParameter("tipoArquivo", arquivo.getTipoArquivo())
									  .uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<ArquivoPagamento> buscarPorCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a FROM ArquivoPagamento a ");
		sql.append("LEFT JOIN FETCH  a.mes m ");
		sql.append("LEFT JOIN FETCH  a.ano ano");
		sql.append("LEFT JOIN FETCH  a.cidade c ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND c = :cidade ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter("cidade", cidade);
		return (List<ArquivoPagamento>) query.list();
	}

	public void imprimirRelatorio(ArquivoPagamento arquivo, List<ArquivoPagamento> arquivos, String nomeRelatorio){
		FacesContext context = FacesContext.getCurrentInstance();
		ServletContext sc = (ServletContext) context.getExternalContext().getContext();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ANO", arquivo.getAno() == null ? "TODOS" : arquivo.getAno().getAno() + "");
		parameters.put("MES", arquivo.getMes() == null ? "TODOS" : arquivo.getMes().getDescricao());
		parameters.put("CIDADE", arquivo.getCidade() == null ? "TODAS" : arquivo.getCidade().getDescricao().toUpperCase());
		parameters.put("TIPO_ARQUIVO", arquivo.getTipoArquivo() == null ? "TODOS" : arquivo.getTipoArquivo().getDescricao());
		parameters.put("LOGO_CIVITAS", sc.getRealPath("/resources/images/logo-login.png"));
		parameters.put("JR_DATA_SOURCE", new JRBeanCollectionDataSource(arquivos));
		
		reportService.execute(parameters, sc.getRealPath("/report/" + nomeRelatorio), Extensao.PDF, false);
	}
}
