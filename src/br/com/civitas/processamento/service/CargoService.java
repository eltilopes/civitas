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
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
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
}
