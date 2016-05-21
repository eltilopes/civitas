package br.com.civitas.arquitetura.seguranca.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.base.LogAcesso;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.vo.LogAcessoVO;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class LogAcessoService extends  AbstractPersistence<LogAcesso> {
	
	private static final long serialVersionUID = 2467389100310838409L;
	public static final String DATA_INICIO = "dataInicio";
	public static final String DATA_FIM = "dataFim";

	@SuppressWarnings("unchecked")
	public List<LogAcesso> findByFilter(Map<String, Object> filter, Integer first, Integer rows){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ace FROM LogAcesso ace ");
		sql.append(" INNER JOIN FETCH ace.usuario usu ");
		getRestricao(filter, sql);
		return getQuery(filter, sql).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private void getRestricao(Map<String, Object> filter, StringBuilder sql){
		
		List<Usuario> usuarios = (List<Usuario>) filter.get(USUARIO);
		sql.append(" WHERE usu.id <> 1 ");
		sql.append(" AND ace.dataAcesso BETWEEN :dataInicio AND :dataFim ");
		sql.append(checkIsNotNull(usuarios) ? " AND usu in (:usuario) " : "");
		sql.append(" ORDER BY ace.id DESC");
	}
	
	@SuppressWarnings("unchecked")
	private Query getQuery(Map<String, Object> filter, StringBuilder sql){
		
		List<Usuario> usuarios = (List<Usuario>) filter.get(USUARIO);
		
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(DATA_INICIO, filter.get(DATA_INICIO));
		query.setParameter(DATA_FIM, new DateTime(filter.get(DATA_FIM)).withHourOfDay(23).withMinuteOfHour(59).toDate());
		
		if(checkIsNotNull(usuarios)){
			query.setParameter(USUARIO, usuarios);
		}
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<LogAcessoVO> buscarMaisAcessados(Map<String, Object> filter){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(la.id) as quantidade FROM log_acesso la ");
		sql.append(" INNER JOIN usuario usu ON usu.id = la.usuario_id ");
		getRestricaoAcessados(filter, sql);
		sql.append(" ORDER BY count(la.id) DESC ");
		return getQueryAcessados(filter, sql).list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<LogAcessoVO> buscarMenosAcessados(Map<String, Object> filter){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(la.id) as quantidade FROM log_acesso la ");
		sql.append(" INNER JOIN usuario usu ON usu.id = la.usuario_id ");
		getRestricaoAcessados(filter, sql);
		sql.append(" ORDER BY count(la.id) ASC ");
		return getQueryAcessados(filter, sql).list();
	}	
	
	@SuppressWarnings("unchecked")
	private void getRestricaoAcessados(Map<String, Object> filter, StringBuilder sql){
		List<Usuario> usuarios = (List<Usuario>) filter.get(USUARIO);
		sql.append(" WHERE la.data_acesso BETWEEN :dataInicio AND :dataFim ");
		sql.append(checkIsNotNull(usuarios) ? " AND usu.id in ( " + convertListToString(usuarios) + " ) " : "");
	}
	
	private org.hibernate.Query getQueryAcessados(Map<String, Object> filter, StringBuilder sql){
		Session session = (Session) getSessionFactory().getCurrentSession();
		org.hibernate.Query query = session.createSQLQuery(sql.toString());
		query.setResultTransformer(Transformers.aliasToBean(LogAcessoVO.class));
		query.setMaxResults(20);
		query.setParameter(DATA_INICIO, filter.get(DATA_INICIO));
		query.setParameter(DATA_FIM, new DateTime(filter.get(DATA_FIM)).withHourOfDay(23).withMinuteOfHour(59).toDate());
		return query;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<LogAcessoVO> buscarUsuarioSemAcesso(Map<String, Object> filter){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT 0 as quantidade, usu.nome as usuario, ");
		sql.append(" (SELECT lg.data_acesso FROM Log_acesso lg WHERE lg.usuario_id = usu.id ORDER BY id DESC LIMIT 1) as data ");
		sql.append(" FROM Usuario usu ");
		sql.append(" LEFT JOIN log_acesso la ON la.usuario_id = usu.id ");
		sql.append(" WHERE usu.id NOT IN (SELECT lg.usuario_id FROM Log_acesso lg WHERE lg.data_acesso BETWEEN :dataInicio AND :dataFim) ");
		sql.append(" GROUP BY  usu.id, usu.nome ");
		sql.append(" ORDER BY usu.nome ");
		Session session = (Session) getSessionFactory().getCurrentSession();
		org.hibernate.Query query = session.createSQLQuery(sql.toString());
		query.setParameter(DATA_INICIO, filter.get(DATA_INICIO));
		query.setParameter(DATA_FIM, new DateTime(filter.get(DATA_FIM)).withHourOfDay(23).withMinuteOfHour(59).toDate());
		query.setResultTransformer(Transformers.aliasToBean(LogAcessoVO.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public LogAcesso buscarUltimoAcesso(Usuario usuario){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT log FROM LogAcesso log ");
		sql.append(" INNER JOIN FETCH log.usuario usu ");
		sql.append(" WHERE usu = :usuario ");
		sql.append(" ORDER BY log.id DESC ");
		List<LogAcesso> list =  getSessionFactory().getCurrentSession().createQuery(sql.toString()).setParameter(USUARIO, usuario).list();
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<LogAcesso> buscarUltimosAcessos(Usuario usuario){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT log FROM LogAcesso log ");
		sql.append(" WHERE log.usuario = :usuario ");
		sql.append(" ORDER BY log.id DESC ");
		return getSessionFactory().getCurrentSession().createQuery(sql.toString()).setParameter(USUARIO, usuario).list();
	}

	@SuppressWarnings("unchecked")
	public List<LogAcesso> buscaPorUsuarioMaisPeriodo(Usuario usuario,Date dataInicial, Date dataFinal) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT la FROM LogAcesso la ")
		   .append("LEFT JOIN FETCH la.usuario usu ")
		   .append("WHERE usu = :usuario  ");
		if(dataInicial != null && dataFinal != null){
			sql.append(" AND la.dataAcesso BETWEEN :dataInicial AND :dataFinal ");
		}
		   sql.append("ORDER BY la.dataAcesso ASC ");

		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString())
                .setParameter("dataInicial", dataInicial)
				.setParameter("dataFinal", dataFinal)
				.setParameter("usuario", usuario);
        
		return query.getResultList();

	}

	@Override
	protected Class getClazz() {
		return LogAcesso.class;
	}
	
}