package br.com.civitas.processamento.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class CargoService extends AbstractPersistence<Cargo> {

	private static final long serialVersionUID = -5274586913255808832L;

	@Override
	protected Class<Cargo> getClazz() {
		return Cargo.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Cargo> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT c FROM Cargo c");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <Cargo> cargos = (List<Cargo>) query.list();
		Collections.sort(cargos, (c1, c2) -> c1.getNumero().compareTo(c2.getNumero()));
		return cargos;
	}

	public Cargo buscarPorNumeroCidadeTipoArquivo(Cargo cargo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM Cargo c ");
		sql.append(" WHERE c.numero = :numero  ");
		sql.append(" AND c.cidade = :cidade  ");
		sql.append(" AND c.tipoArquivo = :tipoArquivo  ");
		
		return (Cargo) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("numero", cargo.getNumero())
									  .setParameter("cidade", cargo.getCidade())
									  .setParameter("tipoArquivo", cargo.getTipoArquivo())
									  .uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Cargo> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM Cargo c ");
		sql.append(" WHERE c.cidade = :cidade  ");
		sql.append(" AND c.tipoArquivo = :tipoArquivo  ");
		sql.append(" ORDER BY length(c.descricao) DESC  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}
	
	public boolean existeCargosInativos(Cargo cargo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM Cargo c ");
		sql.append(" WHERE c.numero = :numero  ");
		sql.append(" AND c.cidade = :cidade  ");
		sql.append(" AND c.tipoArquivo = :tipoArquivo  ");
		sql.append(" AND c.descricao = :descricao ");
		sql.append(" AND c.id <> :id ");
		sql.append(" AND c.ativo = true ");
		
		return getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("numero", cargo.getNumero())
									  .setParameter("cidade", cargo.getCidade())
									  .setParameter("id", cargo.getId())
									  .setParameter("descricao", cargo.getDescricao())
									  .setParameter("tipoArquivo", cargo.getTipoArquivo())
									  .list().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cargo> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM Cargo c ");
		sql.append(" WHERE c.cidade = :cidade  ");
		sql.append(" ORDER BY c.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
				.setParameter("cidade", cidade)
				.list();
	}

	public boolean existeCargosInativos() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c from Cargo c WHERE c.ativo = false ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return !query.list().isEmpty();
	}

	@SuppressWarnings("unchecked")
	public List<Cargo> buscarTodosInativos() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM Cargo c ");
		sql.append("INNER JOIN FETCH c.cidade cid ");
		sql.append(" WHERE c.ativo = false ");
		sql.append(" ORDER BY cid.descricao ASC ");
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString()).list();
	}

	@SuppressWarnings("unchecked")
	public List<Cargo> buscarTipoArquivoCidadeDescricao(Cidade cidade, TipoArquivo tipoArquivo,String descricao) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c FROM Cargo c ");
		sql.append(" WHERE c.cidade = :cidade  ");
		sql.append(" AND c.tipoArquivo = :tipoArquivo  ");
		sql.append(" AND UPPER(c.linhaCargo) LIKE UPPER(:descricaoUpper) ");
		sql.append(" AND UPPER(c.descricao) <> UPPER(:descricao) ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .setParameter("descricaoUpper", "%" + descricao + "%")
									  .setParameter("descricao", descricao)
									  .list();
	}
	
}
