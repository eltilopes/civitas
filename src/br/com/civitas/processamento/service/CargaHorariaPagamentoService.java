package br.com.civitas.processamento.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class CargaHorariaPagamentoService extends AbstractPersistence<CargaHorariaPagamento> {

	private static final long serialVersionUID = 5149399797874554430L;

	@Override
	protected Class<CargaHorariaPagamento> getClazz() {
		return CargaHorariaPagamento.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<CargaHorariaPagamento> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM CargaHorariaPagamento c ");
		sql.append(" WHERE c.cidade = :cidade  ");
		sql.append(" AND c.tipoArquivo = :tipoArquivo  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CargaHorariaPagamento> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM CargaHorariaPagamento c ");
		sql.append(" WHERE c.cidade = :cidade  ");
		sql.append(" ORDER BY c.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
				.setParameter("cidade", cidade)
				.list();
	}
	
}
