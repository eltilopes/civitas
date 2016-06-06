package br.com.civitas.processamento.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class NivelPagamentoService extends AbstractPersistence<NivelPagamento> {

	private static final long serialVersionUID = -7055181734439951026L;

	@Override
	protected Class<NivelPagamento> getClazz() {
		return NivelPagamento.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<NivelPagamento> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT n FROM NivelPagamento n ");
		sql.append(" WHERE n.cidade = :cidade  ");
		sql.append(" AND n.tipoArquivo = :tipoArquivo  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<NivelPagamento> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT n FROM NivelPagamento n ");
		sql.append(" WHERE n.cidade = :cidade  ");
		sql.append(" ORDER BY n.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
				.setParameter("cidade", cidade)
				.list();
	}
	
}
