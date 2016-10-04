package br.com.civitas.processamento.service;

import java.util.List;
import java.util.Objects;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.helpers.utils.StringUtils;
import br.com.civitas.processamento.entity.Matricula;

@Service
public class MatriculaService extends AbstractPersistence<Matricula> {

	private static final long serialVersionUID = -5829554267277214171L;

	@Override
	protected Class<Matricula> getClazz() {
		return Matricula.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Matricula> getMatriculaPorNomeCidade(Matricula matricula) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT m FROM Matricula m ");
		sql.append("LEFT JOIN FETCH m.unidadeTrabalho ut ");
		sql.append("LEFT JOIN FETCH m.nivelPagamento np ");
		sql.append("LEFT JOIN FETCH m.cargaHorariaPagamento chp ");
		sql.append("LEFT JOIN FETCH m.cargo ca ");
		sql.append("LEFT JOIN FETCH m.secretaria sec ");
		sql.append("LEFT JOIN FETCH m.setor setor ");
		sql.append("LEFT JOIN FETCH m.vinculo v ");
		sql.append("LEFT JOIN FETCH sec.cidade c ");
		sql.append("WHERE 1 = 1 ");
		sql.append(Objects.nonNull(matricula.getSecretaria().getCidade()) ? "AND c = :cidade " : "");
		sql.append(StringUtils.notNullOrEmpty(matricula.getNomeFuncionario()) ? "AND UPPER(m.nomeFuncionario) LIKE UPPER('%"+ matricula.getNomeFuncionario() +"%' ) " : "");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		if(Objects.nonNull(matricula.getSecretaria().getCidade())){
			query.setParameter("cidade", matricula.getSecretaria().getCidade());
		}
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Matricula> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT m FROM Matricula m");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<Matricula>) query.list();
	}
	
	public Matricula salvar(Matricula matricula){
		getSession().merge(matricula.getVinculo());
		getSession().merge(matricula.getCargo());
		return save(matricula);
	}

}
