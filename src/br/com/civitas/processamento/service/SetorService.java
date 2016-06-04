package br.com.civitas.processamento.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class SetorService extends AbstractPersistence<Setor> {

	private static final long serialVersionUID = -6800240100220403164L;

	@Override
	protected Class<Setor> getClazz() {
		return Setor.class;
	}
	
	public Setor buscarPorDescricaoCidadeTipoArquivo(Setor setor) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT s FROM Setor s ");
		sql.append(" WHERE s.descricao = :descricao ");
		sql.append(" AND s.cidade = :cidade  ");
		sql.append(" AND s.tipoArquivo = :tipoArquivo  ");
		
		return (Setor) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("descricao", setor.getDescricao())
									  .setParameter("cidade", setor.getCidade())
									  .setParameter("tipoArquivo", setor.getTipoArquivo())
									  .uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Setor> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT s FROM Setor s ");
		sql.append(" WHERE s.cidade = :cidade  ");
		sql.append(" AND s.tipoArquivo = :tipoArquivo  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Setor> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT s FROM Setor s ");
		sql.append(" WHERE s.cidade = :cidade  ");
		sql.append(" ORDER BY s.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
				.setParameter("cidade", cidade)
				.list();
	}
}
