package br.com.civitas.processamento.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class SecretariaService extends AbstractPersistence<Secretaria> {

	private static final long serialVersionUID = 6562254861451931875L;

	@Override
	protected Class<Secretaria> getClazz() {
		return Secretaria.class;
	}
	
	public Secretaria buscarPorDescricaoCidadeTipoArquivo(Secretaria secretaria) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT s FROM Secretaria s ");
		sql.append(" WHERE s.descricao = :descricao ");
		sql.append(" AND s.cidade = :cidade  ");
		sql.append(" AND s.tipoArquivo = :tipoArquivo  ");
		
		return (Secretaria) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("descricao", secretaria.getDescricao())
									  .setParameter("cidade", secretaria.getCidade())
									  .setParameter("tipoArquivo", secretaria.getTipoArquivo())
									  .uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Secretaria> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT s FROM Secretaria s ");
		sql.append(" WHERE s.cidade = :cidade  ");
		sql.append(" AND s.tipoArquivo = :tipoArquivo  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Secretaria> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT s FROM Secretaria s ");
		sql.append(" WHERE s.cidade = :cidade  ");
		sql.append(" ORDER BY s.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
				.setParameter("cidade", cidade)
				.list();
	}
	
}
