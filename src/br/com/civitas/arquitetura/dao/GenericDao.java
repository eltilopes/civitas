package br.com.civitas.arquitetura.dao;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import br.com.civitas.arquitetura.annotations.OrderBy;
import br.com.civitas.arquitetura.entity.Entidade;

/**
 * Gerenciador genérico de acesso ao banco de dados. Contém métodos básicos de operações com o banco de dados não transacionados.
 * @author Elias Sales, Samuel Soares
 *
 * @param <E> Entidade, que extende genericcrud.model.Entidade, manipulada no banco de dados.
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("dao")
public class GenericDao<E extends Entidade> implements Serializable{
	
	private static final long serialVersionUID = 4856023302036762155L;

	@Resource(name = "entityManager")
	private HibernateTemplate entityManager;
	
	@Resource(name = "dataSource")
	private DataSource dataSource;
	
	
	/**
	 * Consulta todas as tuplas da entidade no banco de dados na ordem em que eles estão no banco. 
	 * @param clazz Classe para consulta
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela. 
	 */
	public List<E> findAll( Class clazz ) {

		if (clazz.isAnnotationPresent(OrderBy.class)) {
			String valor = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
			if (valor != null && !"".equals(valor)) {
				return findAll(clazz, valor);
			} 
		} 

		return getEntityManager().find( "from " + clazz.getName());//e.g. from Entidade
	}

	/**
	 * Consulta todas as tuplas da entidade no banco de dados na ordem definida em campoOrdenacao.
	 * @param clazz Classe para consulta
	 * @param campoOrdenacao Atributos que farão a ordenação dos dados. Se houver mais de um campo de ordenação, 
	 * 		eles terão que estar separados por vírgula numa mesma string <br/>  
	 * 		<p>E.g.: Classe carro. <br/>
	 * 			Ordenação pelo campo placa - findAll( Carro.class, "placa" ); <br/>
	 * 			Ordenação pelos campos cor e placa - findAll( Carro.class, "cor, placa" ); <br/>
	 * 			Ordenação pelos campos cor e placa em ordem descendente - findAll( Carro.class, "cor, placa desc" ).</p>		  
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela ordenados de acordo com a definição de campoOrdenacao.
	 */
	public List<E> findAll( Class clazz, String campoOrdenacao ) {
		return getEntityManager().find( "from " + clazz.getName() + " order by " + campoOrdenacao );
	}

	/**
	 * Consulta todas as tuplas da entidade no banco baseado nos atributos preenchidos do objeto. 
	 * Obs.: Se é feita consulta com o operador =; like não é utilizado.
	 * @param obj Objeto com dados para consulta.
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela que satisfazem os dados do objeto.
	 */
	public List<E> findByExample(E obj) {
		return getEntityManager().findByExample( obj );
	}

	/**
	 * Consulta uma entidade pela sua chave primária.
	 * @param clazz Classe para consulta.
	 * @param id Id do objeto.
	 * @return Objeto que possui o id correspondente, null se não for encontrado o id.
	 */
	public E findByPrimaryKey(Class clazz, Serializable id) {
		return (E)getEntityManager().get(clazz, id);
	}

	/**
	 * Salva um objeto no banco de dados.
	 * @param obj Objeto a ser salvo.
	 * @return Objeto salvo.
	 */
	public E insert(E obj) {
		getEntityManager().save( obj );
		return obj;
	}

	/**
	 * Salva uma coleção de objetos no banco de dados. Se algum objeto da coleção existir no banco (id preenchido), ele é atualizado. 
	 * @param collection Coleção a ser salva.
	 */
	public void insertAll(Collection<E> collection) {
		getEntityManager().saveOrUpdateAll( collection );		
	}

	/**
	 * Remove um objeto do banco de dados.
	 * @param obj Objeto a ser removido.
	 */
	public void remove(E obj) {
		getEntityManager().delete(obj);		
	}

	/**
	 * Remove uma coleção de objetos do banco de dados.
	 * @param collection Coleção a ser removida.
	 */
	public void removeAll(Collection<E> collection) {
		getEntityManager().deleteAll(collection);		
	}

	/**
	 * Atualiza um objeto no banco de dados. Se o objeto não existir (id nulo) ele é salvo.
	 * @param obj Objeto a ser atualizado.
	 * @return Objeto atualizado. 
	 */
	public E update(E obj) {
		getEntityManager().saveOrUpdate(obj);
		return obj;
	}

	/**
	 * Atualiza uma coleção de objetos no banco de dados. Se algum objeto da coleção não existir no banco (id nulo), ele é salvo.
	 * @param collection Coleção a ser atualizada.
	 */
	public void updateAll(Collection<E> collection) {
		getEntityManager().saveOrUpdateAll(collection);		
	}

	/**
	 * Consulta todas as tuplas da entidade cuja descrição começa com strStart. Os dados são ordenados pela descrição. 
	 * @param clazz Classe para consulta.
	 * @param strStart Valor da descrição para consulta.
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela cuja descrição começa com strStart ordenados pela descrição.
	 */
	public List<E> startsWith( Class<E> clazz, String strStart ){
		StringBuilder query = new StringBuilder();
		
		query.append("from " + clazz.getName() + 
				" where descricao like '" + strStart.toLowerCase() + "%' or descricao like '" + strStart.toUpperCase() + "%'");
		
		if (clazz.isAnnotationPresent(OrderBy.class)) {
			String ordenacao = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
			if (ordenacao != null && !"".equals(ordenacao)) {
				query.append( " order by " + ordenacao );
			} 
		} 
		
		return getEntityManager().find(query.toString());
	}

	/**
	 * Consulta todas as tuplas da entidade cuja descrição começa com strStart e que satisfazem os dados do filtro filter.
	 * Este filtro refere-se a atributos da classe com dados preenchidos. Os dados s�o ordenados pela descrição.
	 * @param clazz Classe para consulta.
	 * @param strStart Valor da descrição para consulta.
	 * @param filter Filtro para consulta. A chave corresponde ao nome do atributo; para cada chave deve estar associado um valor.  
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela cuja descrição começa com strStart filtrados pelos atributos em filter ordenados pela descrição.
	 */
	public List<E> startsWithAndFilter( Class<E> clazz, String strStart, Map<String, Object> filter ){

		StringBuilder query = new StringBuilder();
		query.append( "from " + clazz.getName() + " where ( descricao like '" + strStart.toLowerCase() + "%' or descricao like '" + strStart.toUpperCase() + "%' )" );
		Set<String> setKey = filter.keySet();
		for( String key : setKey ){
			
			Object valor = filter.get( key );
			
			if( valor instanceof String || valor instanceof Character ){
				query.append( " and ( " + key + " like '%" + valor.toString().toLowerCase() + "%' or " + key + " like '%" + valor.toString().toUpperCase() + "%') ");
			}
			else if( valor instanceof Date ){
				SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
				query.append( " and " + key + " = '" + df.format(valor) + "' ");
			}else{
				query.append( " and " + key + " = " + valor );
			}
			
			if (clazz.isAnnotationPresent(OrderBy.class)) {
				String ordenacao = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
				if (ordenacao != null && !"".equals(ordenacao)) {
					query.append( " order by " + ordenacao );
				} 
			} 
			
		}		
		return getEntityManager().find( query.toString() );
	} 

	/**
	 * Consulta todas as tuplas da entidade que satisfazem os dados do filtro filter.
	 * Este filtro refere-se a atributos da classe com dados preenchidos.
	 * @param clazz Classe para consulta.
	 * @param filter Filtro para consulta. A chave corresponde ao nome do atributo; para cada chave deve estar associado um valor.
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela filtrados pelos atributos em filter.
	 */
	public List<E> findByFilter( Class<E> clazz, Map<String, Object> filter ){
		String valor = null;
		if (clazz.isAnnotationPresent(OrderBy.class)) {
			valor = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
		} 
		
		String query = montaQuery(clazz, filter, valor);		
		return getEntityManager().find( query );
	}	

	/**
	 * Consulta todas as tuplas da entidade que satisfazem os dados do filtro filter e ordena o resultado pelos atributos de campoOrdenacao.
	 * O filtro refere-se a atributos da classe com dados preenchidos.
	 * @param clazz Classe para consulta.
	 * @param filter Filtro para consulta. A chave corresponde ao nome do atributo; para cada chave deve estar associado um valor.
	 * @param campoOrdenacao Atributos que farão a ordenação dos dados. Se houver mais de um campo de ordenação, 
	 * 		eles terão que estar separados por vírgula numa mesma string <br/>  
	 * 		<p>E.g.: Classe Carro e fitro filter. <br/>
	 * 			Ordenação pelo campo placa - findByFilter( Carro.class, filter, "placa" ); <br/>
	 * 			Ordenação pelos campos cor e placa - findByFilter( Carro.class, filter, "cor, placa" ); <br/>
	 * 			Ordenação pelos campos cor e placa em ordem descendente - findByFilter( Carro.class, filter, "cor, placa desc" ).</p>
	 * @return Lista com todos os objetos correspondentes às tuplas da tabela filtrados pelos atributos em filter.
	 */
	public List<E> findByFilter( Class<E> clazz, Map<String, Object> filter, String campoOrdenacao ){
		String query = montaQuery(clazz, filter, campoOrdenacao);
		return getEntityManager().find( query.toString() );
	}	

	/**
	 * Monta a query para a entidade baseada no filtro filter e, se houver, no campo de ordenação.
	 * @param clazz Classe para montagem da query.
	 * @param filter Filtro usando na cláusula where.
	 * @param campoOrdenacao Campos de ordenação.
	 * @return Query montada.
	 */
	private String montaQuery( Class<E> clazz, Map<String, Object> filter, String campoOrdenacao ){
		
		StringBuilder query = new StringBuilder();
		
		query.append( "from " + clazz.getName() + " where 1=1 " );
		Set<String> setKey = filter.keySet();
		for( String key : setKey ){
			Object valor = filter.get( key );
			
			if( valor instanceof String || valor instanceof Character ){
				query.append( " and ( lower(" + key + ") like lower('%" + valor.toString() + "%')) ");
			}
			else if( valor instanceof Date ){
				SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
				query.append( " and " + key + " = '" + df.format(valor) + "' ");
			}else{
				query.append( " and " + key + " = " + valor );
			}
		}
		if( campoOrdenacao != null )
			query.append( " order by " + campoOrdenacao );
		return query.toString();
	}
	

	public HibernateTemplate getEntityManager() {return entityManager;}
	public void setEntityManager(HibernateTemplate entityManager) {this.entityManager = entityManager;}

	public DataSource getDataSource() {return dataSource;}
	public void setDataSource(DataSource dataSource) {this.dataSource = dataSource;}

	
}