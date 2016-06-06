package br.com.civitas.processamento.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class UnidadeTrabalhoService extends AbstractPersistence<UnidadeTrabalho> {

	private static final long serialVersionUID = 7064203713233501920L;

	@Override
	protected Class<UnidadeTrabalho> getClazz() {
		return UnidadeTrabalho.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<UnidadeTrabalho> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT u FROM UnidadeTrabalho u ");
		sql.append(" WHERE u.cidade = :cidade  ");
		sql.append(" AND u.tipoArquivo = :tipoArquivo  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<UnidadeTrabalho> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT u FROM UnidadeTrabalho u ");
		sql.append(" WHERE u.cidade = :cidade  ");
		sql.append(" ORDER BY u.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
				.setParameter("cidade", cidade)
				.list();
	}
	
}
