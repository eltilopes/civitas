package br.com.civitas.arquitetura.service;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ServiceCrud<Entity> extends Service
{

	/**
	 * Insere o objeto informado.
	 * @param obj Objeto a ser inserido.
	 * @return Objeto inserido.
	 */
	public Entity insert(Entity obj);

	/**
	 * Insere todos os objetos de collection.
	 * @param collection Lista de objetos a serem inseridos.
	 *
	 */
	public void insertAll(Collection<Entity> collection);
	
	/**
	 * Atualiza o objeto informado.
	 * @param obj Objeto a ser atualizado.
	 * @return Objeto atualizado.
	 */
	public Entity update(Entity obj);
	
	/**
	 * Atualiza todos os objetos de collection.
	 * @param collection Lista de objetos a serem atualizados.
	 *
	 */
	public void updateAll(Collection<Entity> collection);
	
	/**
	 * Remove o objeto informado.
	 * @param obj Objeto a ser removido.
	 *
	 */
	public void remove(Entity obj);
	
	/**
	 * Remove todos os objetos de collection.
	 * @param collection Lista de objetos a serem exclu�dos.
	 *
	 */
	public void removeAll(Collection<Entity> collection);
	
	/**
	 * Retorna um objeto baseado em sua chave primária.
	 * @param obj Chave primária do objeto.
	 * @return Entity Objeto encontrado.
	 *
	 */
	public Entity findByPrimaryKey(Class<Entity> clazz, Serializable id);
	
	/**
	 * Retorna todos os objetos Entity.
	 * @return Lista de objetos Entity.
	 *
	 */
	public List<Entity> findAll( Class<Entity> clazz );
	
	/**
	 * Retorna todos os objetos ordenados por campoOrdenacao.
	 * @param clazz Classe de consulta.
	 * @param campoOrdenacao Campos de ordenação.
	 * @return Lista de objetos Entity.
	 */
	public List<Entity> findAll( Class<Entity> clazz, String campoOrdenacao );
	
	/**
	 * Retorna uma lista de Entity baseado nos atributos do argumento obj
	 * @param obj Objeto usado na consulta.
	 * @return Lista de Entity com os mesmos valores de obj.
	 */
	public List<Entity> findByExample(Entity obj);

	/**
	 * Retorna uma lista de Entity cuja descrição inicia com determinado padrão.
	 * @param clazz Classe para consulta.
	 * @param strStart Parte inicial da descrição. 
	 * @return Lista de Entity cuja descrição inicia com strStart.
	 */
	public List<Entity> startsWith( Class<Entity> clazz, String strStart );

	/**
	 * Retorna uma lista de Entity cuja descrição inicia com determinado padrão, filtrado por valores de filter.
	 * @param clazz Classe para consulta.
	 * @param strStart Parte inicial da descrição.
	 * @param filter Filtro (pares chave-valor) usado na consulta.
	 * @return Lista de Entity cuja descrição inicia com strStart filtrada por filter.
	 */
	public List<Entity> startsWithAndFilter( Class<Entity> clazz, String strStart, Map<String, Object> filter );
	
	/**
	 * Retorna uma lista de Entity que obedece ao filtro informado.
	 * @param clazz Classe para consulta.
	 * @param filter Filtro (pares chave-valor) usado na consulta.
	 * @return Lista de Entity filtrada por filter.
	 */
	public List<Entity> findByFilter( Class<Entity> clazz, Map<String, Object> filter );
	
	/**
	 * Retorna uma lista de Entity que obedece ao filtro informado ordenada pelo campoOrdenacao.
	 * @param clazz Classe para consulta
	 * @param filter Filtro (pares chave-valor) usado na consulta.
	 * @param campoOrdenacao Campos de ordenaçao.
	 * @return Lista de Entity filtrada por filter e ordenada pelos atributos de campoOrdenacao.
	 */
	public List<Entity> findByFilter(Class<Entity> clazz, Map<String, Object> filter, String campoOrdenacao);
	
	/**
	 * Seleciona a forma de consulta de acordo com os par�metros informados.
	 * @param obj Objeto de consulta.
	 * @param strStart Parte inicial de uma descrição.
	 * @param filter Filtro (pares chave-valor) usado na consulta.
	 * @return Lista de Entity que atendem os requisitos de consulta.
	 */
	public List<Entity> find( Entity obj, String strStart, Map<String, Object> filter );
}