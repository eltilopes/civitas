package br.com.civitas.arquitetura.controller;

import static br.com.civitas.arquitetura.util.FacesUtils.addErrorMessage;
import static br.com.civitas.arquitetura.util.FacesUtils.addInfoMessage;
import static br.com.civitas.arquitetura.util.FacesUtils.addWarnMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.entity.Entidade;
import br.com.civitas.arquitetura.service.ServiceCrud;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.arquitetura.util.PropertiesUtils;
import br.com.civitas.arquitetura.util.ReflectionUtil;


/**
 * Superclasse para os managed beans. Oferece os métodos básicos para manipulação de uma entidade e 
 * manutenção de estados de uma página.
 * @author Elias Sales, Samuel Soares
 * @since 29/05/2009
 * @param <Entity> Entidade que será manipulada.
 * @param <Service> Serviço que manipula a entidade.
 */
public abstract class AbstractCrudBean<Entity extends Entidade, Service extends ServiceCrud<Entity>>{
	//TODO colocar logs
	protected static Log logger = LogFactory.getLog("");
	
	/**Local onde fica o arquivo properties de mensagens.*/
	private String fileMessages = "/modelo/src/MessagesResources.properties";
	
	/**Properties de mensagens, carregado com o arquivo configurado em fileMessages.*/
	private Properties messageProperties;
	
	/**
	 * Mantém a página no estado de edição.
	 */
	public static final String STATE_EDIT = "edit";

	/**
	 * Mantém a página no estado de inserção.
	 */
	public static final String STATE_INSERT = "insert";

	/**
	 * Mantém a página no estado de exclusão.
	 */
	public static final String STATE_DELETE = "delete";

	/**
	 * Mantém a página no estado de visualização.
	 */
	public static final String STATE_VIEW = "view";	

	/**
	 * Mantém a página no estado de consulta. Esse é o estado padrão.
	 */
	public static final String STATE_SEARCH = "search";

	/**
	 * Atributo que indica qual o estado atual da página.
	 */
	private String currentState;
	
	/**
	 * Entidade de cadastro e atualização.
	 */
	private Entity entity;	
	
	/**
	 * Entidade de consulta. Necessário para manter os dados da consulta quando alternando entre estados. 
	 */
	private Entity entitySearch;
	
	/**
	 * Entidade que pode ser utilizada para referenciar um objeto selecionado de uma lista.
	 */
	private Entity selectEntity;

	/**
	 * Opção de filtro, sobre uma lista consultada, selecionada pelo usuário.
	 */
	// private String letraPesquisa;
	
	/**
	 * HtmlDatascroller da lista principal do estado de consulta. Só pode ser utilizado num único dataTable. 
	 * Para utilizar em outro dataTable, deve ser criado outro objeto HtmlDatascroller. 
	 */
	// private HtmlDatascroller scroller = new HtmlDatascroller(); //TODO pendente implementar nova versão para o dataScroller.
	
	/**
	 * Mantém o número da página do HtmlDatascroller principal.
	 * Tem que ter um valor para poder funcionar na primeira vez.
	 */
	private Integer numeroPagina = 1;
	
	/**
	 * Lista entidades de uma consulta.
	 */
	private DataModel<Entity> resultSearch;
	
	/**
	 * Lista de entidades que serão inseridas, atualizadas ou removidas. 
	 */
	private DataModel<Entity> entities;
	
	/**
	 * Usado na manutenção do resultado original da consulta feita, já que a lista resultSearch poderá variar de acordo
	 * com a filtragem realizada pelo usuário.
	 */
	private List<Entity> originalResult;
	
	/**
	 * Serviço usado para manipulação dos dados enviados numa requisição do usuário.
	 */
	@ManagedProperty("#{genericService}")
	protected Service service;
	
	/**
	 * Inicializa as listas e entidades. Coloca no estado de consulta.
	 */
	public AbstractCrudBean() {
		resultSearch = new ListDataModel<Entity>();
		entities = new ListDataModel<Entity>();
		originalResult = new ArrayList<Entity>();
		entity = getNewEntityInstance();
		entitySearch = getNewEntityInstance();
		setCurrentState( STATE_SEARCH );
	}
	
	/**
	 * Cria uma lista de SelectItem com todos os dados da entidade ordenados pela descricao.
	 * @return Lista de SelectItem.
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAllSelectItem() {				
		return  FacesUtils.beanToSelectItem( (List<Entidade> )getService().findAll((Class<Entity>)entity.getClass(), "descricao" ) ) ;
	}
	
	/**
	 * Seleciona um Entity entre o resultado da resultSearch
	 * @param event
	 */
	public void selectEntity( ActionEvent event ){
		setSelectEntity((Entity)getResultSearch().getRowData());
	}
	
	/**
	 * Pega uma mensagem do properties.
	 * @param key
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getMessage( String key ){
		if( messageProperties == null ){
			PropertiesUtils pUtils = new PropertiesUtils();
			messageProperties = pUtils.getProperties( getFileMessages() );
		}
		return (String)getMessageProperties().get(key);
	}

	/**
	 * Manda salvar uma entidade.
	 * @param event
	 */
	public void save( ActionEvent event ){
		try {
			executeSave();
		} catch (ApplicationException e) {
			e.printStackTrace();
			addErrorMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			addErrorMessage(getMessage("ERROR_INSERT"));
		}
	}
	
	public void saveAndEdit( ActionEvent event ){
		try {
			executeSaveAndEdit();
		} catch (ApplicationException e) {
			e.printStackTrace();
			addErrorMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			addErrorMessage(getMessage("ERROR_INSERT"));
		}
	}
	
	/**
	 * Manda remover uma entidade, retira-a da lista de consulta e volta para o estado de consulta.
	 * @param event
	 */
	public void delete( ActionEvent event ){
		try{
			executeDelete();
		}catch (ApplicationException e) {
			e.printStackTrace();
			addErrorMessage( e.getMessage() );
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_REMOVE" ) );
		}
	}  
	
	/**
	 * Manda atualizar uma entidade e volta para o estado de consulta.
	 * @param event
	 */
	public void update( ActionEvent event ){
		try{
			executeUpdate();
		} catch (ApplicationException e) {
			e.printStackTrace();
			addErrorMessage(e.getMessage());	
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_UPDATE" ) );
		}
	}
	
	/**
	 * Consulta uma entidade pela sua chave prim�ria.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void findByPrimaryKey( ActionEvent event ){
		try{
			limpaListas();
			 
			Entity res = service.findByPrimaryKey( (Class<Entity>)entitySearch.getClass(), ReflectionUtil.valueId(entitySearch) );
			if( res != null ){
				getResultSearch().setWrappedData( new ArrayList<Entity>() );
				((List<Entity>)getResultSearch().getWrappedData()).add(res);
			}else{
				addWarnMessage( getMessage( "NOT_FOUND" ) );
			}
			setCurrentState(STATE_SEARCH);
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
	}
	
	/**
	 * Consulta as entidade que possuem os parâmetros informados, tais quais foram informados.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void findByExample( ActionEvent event ){
		try{
			limpaListas();
			List<Entity> list = service.findByExample( entitySearch );
			if( !list.isEmpty() ){
				getResultSearch().setWrappedData(list);
				setOriginalResult( (List<Entity>)getResultSearch().getWrappedData() );
			}else{
				addWarnMessage( getMessage( "NOT_FOUND" ) );
			}
			setCurrentState(STATE_SEARCH);
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
	}
	
	/**
	 * Consulta todas as entidades.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void findAll( ActionEvent event ){	
		try{
			limpaListas();
			List<Entity> list = service.findAll( (Class<Entity>)entitySearch.getClass() );
			if( !list.isEmpty() ){
				getResultSearch().setWrappedData(list);
				setOriginalResult( (List<Entity>)getResultSearch().getWrappedData() );
			}else{
				addWarnMessage( getMessage( "NOT_FOUND" ) );
			}
			setCurrentState(STATE_SEARCH);
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
	}
	
	/**
	 * Consulta as entidades que satisfazem os parâmetros informados em notEmptyFields do objeto.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void findByFilter( ActionEvent event ){
		try{
			limpaListas();
			List<Entity> list = null;
			list = service.find( entitySearch, null, entitySearch.notEmptyFields() );
			getResultSearch().setWrappedData(list);
			setOriginalResult( (List<Entity>)getResultSearch().getWrappedData() );
			setCurrentState(STATE_SEARCH);
		}catch (ApplicationException e) {
			addWarnMessage( e.getMessage() );
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
		// letraPesquisa = null;
	}	
	
	/**
	 * Seleciona o tipo de consulta que será realizada, de acordo com os parâmetros informados.
	 * @param event
	 */
	public void find( ActionEvent event ){
		Map<String, Object> map = entitySearch.notEmptyFields();
		if( map == null || map.isEmpty() ){
			findAll( event );
		} else {
			if (ReflectionUtil.getNameFieldId(entitySearch) != null) {
				findByPrimaryKey(event);
			} else {
				findByFilter(event);
			}
		}
	}
	
	/**
	 * Salva todas as entidades que estão na lista entities.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void saveAll( ActionEvent event ){
		try{
			service.insertAll( (List<Entity>)entities.getWrappedData() );
			addInfoMessage( getMessage( "SUCCESS_ITENS_INSERT" ) );
			setCurrentState( STATE_EDIT );
			getEntities().setWrappedData(null);
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
	}
	
	/**
	 * Remove todas as entidades que estão lista entities e atualiza a lista de resultados.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void deleteAll( ActionEvent event ){
		try{
			if( getEntities().getRowCount() > 0 ){
				service.removeAll( (List<Entity>)entities.getWrappedData() );
				
				//Remove os dados das lista de consultas.
				originalResult.removeAll( (List<Entity>)entities.getWrappedData() );
				((List<Entity>)resultSearch.getWrappedData()).removeAll( (List<Entity>)entities.getWrappedData() );
				
				addInfoMessage( getMessage( "SUCCESS_ITENS_REMOVE" ) );
				setCurrentState( STATE_SEARCH );
			}
		}catch (ApplicationException e) {
			find(event);
			e.printStackTrace();
			addWarnMessage( e.getMessage() );
		}
		catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
		getEntities().setWrappedData( null );
	}
	
	/**
	 * Atualiza todas as entidades que estão na lista entities.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void updateAll( ActionEvent event ){
		try{
			service.updateAll( (List<Entity>)entities.getWrappedData() );
			addInfoMessage( getMessage( "SUCCESS_ITENS_UPDATE" ) );
			setCurrentState( STATE_EDIT );
			getEntities().setWrappedData( null );
		}catch (Exception e) {
			e.printStackTrace();
			addErrorMessage( getMessage( "ERROR_MESSAGE" ) );
		}
	}

	/**
	 * Filtra a lista de resultados da consulta pelo caractere inicial da descrição.
	 * @param event
	 */
/*	public void filterByLetter( ActionEvent event ){
		List<Entity> list = getOriginalResult();
		List<Entity> filtro = new ArrayList<Entity>();
		if( letraPesquisa.equals("Todas") ){//Se tiver escolhido a op��o "Todas", coloca a lista da consulta original.
			getResultSearch().setWrappedData( getOriginalResult() );
		}else{//Filtra.
			for( Entity e : list ){
				if( e.getDescricao().toUpperCase().startsWith( letraPesquisa )  ){
					filtro.add(e);
				}
			}
			getResultSearch().setWrappedData( filtro );
		}
	}*/
	
	/**
	 * Lista os caracteres alfanuméricos (além da opção 'Todas') usados para filtragem do resultado de uma consulta.
	 * @return
	 */
	public List<String> getAlphaNumericList(){
		List<String> lista = new ArrayList<String>();
		lista.add("Todas");
		for( int i = 0; i < 10; i++ ){//Algarismos de 0 a 9.
			lista.add( String.valueOf( i ) );
		}
		for( int i = 65; i <= 90; i++ ){//Letras mai�sculas de A a Z.
			lista.add( String.valueOf( ( char ) i ) );
		}
		return lista;
	}
		
	/**
	 * Inclue na lista entities a entidade selecionada num checkbox.
	 * @param event Evento disparado quando há uma modificação no valor do checkbox.
	 */
	@SuppressWarnings("unchecked")
	public void select(  ValueChangeEvent event ){   
		Entity e = (Entity)getResultSearch().getRowData();
		int ind = getResultSearch().getRowIndex();
		if( event.getNewValue().equals( true ) ){
			if( getEntities().getWrappedData() == null ){
				getEntities().setWrappedData( new ArrayList<Entity>() );
			}
			( (List<Entity>)entities.getWrappedData() ).add( e );
		}else if( entities.getRowCount() > 0 && ind < entities.getRowCount() ){
			( (List<Entity>)entities.getWrappedData() ).remove( ind );
		}
	}
	
	/**
	 * Lista as páginas do scoller de um dataTable.
	 * @return
	 */
	/*public List<SelectItem> getPaginasScroller(){
		List<SelectItem> lista = new ArrayList<SelectItem>();
		
		for( int i = 1; i <= scroller.getPageCount(); i++ ){
			lista.add( new SelectItem( i, String.valueOf(i) ) );
		}
		return lista;
	}*/
	
	/**
	 * Retorna <code>true</code> se o estado estiver em edição.
	 * @return
	 */
	public boolean isEditing(){
		return STATE_EDIT.equals(getCurrentState());
	}
	
	/**
	 * Retorna <code>true</code> se o estado estiver em inserção.
	 * @return
	 */
	public boolean isInserting(){
		return STATE_INSERT.equals(getCurrentState());
	}
	
	/**
	 * Retorna <code>true</code> se o estado estiver em exclusão.
	 * @return
	 */
	public boolean isDeleting(){
		return STATE_DELETE.equals(getCurrentState());
	}
	
	/**
	 * Retorna <code>true</code> se o estado estiver em visualização.
	 * @return
	 */
	public boolean isViewing(){
		return STATE_VIEW.equals(getCurrentState());
	}
	
	/**
	 * Retorna <code>true</code> se o estado estiver em atualização, ou seja,
	 * caso o estado esteja em edição ou inserção ou exclusão.
	 * @return
	 */
	public boolean isUpdating() {
		return (this.isEditing() || this.isInserting() || this.isDeleting());
	}
	
	/**
	 * Retorna <code>true</code> se o estado estiver em busca.
	 * @return
	 */
	public boolean isSearching(){
		return (getCurrentState() == null || STATE_SEARCH.equals(getCurrentState()));
	}
		
	/**
	 * Prepara a tela para atualizar uma entidade.
	 * @param event
	 */
	public void prepareUpdate(ActionEvent event){		
		// Alterna os panels necessários para mostrar o conte�do da atualização.
		entity = (Entity)getResultSearch().getRowData();
		setCurrentState(STATE_EDIT);
	}
	
	/**
	 * Prepara a tela para inserir uma entidade.
	 * @param event
	 */
	public void prepareInsert(ActionEvent event){		
		// Alterna os panels necessários para mostrar o conteúdo da inserção.		
		setCurrentState(STATE_INSERT);
		limpaListas();
		setEntity(getNewEntityInstance());
	}
	
	/**
	 * Cancela a inclusão / alteração.
	 * @param event
	 */
	public void cancel(ActionEvent event) {
		// Cancelar as operações, volta para a consulta
		init();
		FacesUtils.refresh();
	}
		
	/**
	 * Prepara a tela para excluir uma entidade.
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void prepareDelete(ActionEvent event){
		// Alterna os panels necessários para mostrar o conteúdo da exclusão.
		entity = (Entity)getResultSearch().getRowData();
		int ind = getResultSearch().getRowIndex();
		((List<Entity>)getResultSearch().getWrappedData()).remove( ind ); //Remove o item a ser excluído da lista de consulta
		setCurrentState(STATE_DELETE);		
	}
		
	/**
	 * Processa a inicialização dos dados
	 */
	public void init(){		
		this.setCurrentState(STATE_SEARCH);
		this.numeroPagina = 1;
		this.limpaListas();
		this.setEntity(getNewEntityInstance());			
		this.setEntitySearch(getNewEntityInstance());
		this.selectEntity( null );
	}
	
	/**
	 * Limpa as listas deste managed bean.
	 */
	protected void limpaListas(){
		this.getResultSearch().setWrappedData( null );	
		this.getOriginalResult().clear();
		this.getEntities().setWrappedData( null );
	}
	
	/**
	 * Evento que dispara a inicialização. Pode ser usado por um ActionSource (botão, link, menu ou similar) 
	 * para inicar a implementação de um caso de uso que faça parte da hierarquia dessa classe.
	 * @param event
	 */
	public void init(ActionEvent event){
		init();
	}

	@SuppressWarnings("unchecked")
	/**
	 * Instancia um novo objeto de acordo 
	 * com o subtipo da Entity.
	 */
	protected Entity getNewEntityInstance(){
		try {
			return (Entity) ((Class<Entity>) ((java.lang.reflect.ParameterizedType) this
					.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]).newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param Entity the Entity to set
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * @return the Entity
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * @return the selectEntity
	 */
	public Entity getSelectEntity() {
		return selectEntity;
	}

	/**
	 * @param selectEntity the selectEntity to set
	 */
	public void setSelectEntity(Entity selectEntity) {
		this.selectEntity = selectEntity;
	}
	
	/**
	 * 
	 * @param resultadoConsulta
	 */
	public void setResultSearch(DataModel<Entity> resultadoConsulta) {
		this.resultSearch = resultadoConsulta;
	}
	/**
	 * 
	 * @return
	 */
	public DataModel<Entity> getResultSearch() {
		return resultSearch;
	}
	
	/**
	 * 
	 * @param estadoCorrente
	 */
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public String getCurrentState() {
		return currentState;
	}
	
	/**
	 * Entity que parametriza a consulta
	 * @param EntityConsulta the EntityConsulta to set
	 */
	public void setEntitySearch(Entity entitySearch) {
		this.entitySearch = entitySearch;
	}
	/**
	 * @return the EntityConsulta
	 */
	public Entity getEntitySearch() {
		return entitySearch;
	}
	
	@SuppressWarnings("unused")
	private boolean mensagemErro;
	
	/**
	 * @return the mensagemErro
	 */
	public boolean isMensagemErro() {		
		if (FacesUtils.isMessageErro()) {
			return true;
		}
		return false;
	}
	/**
	 * @param mensagemErro the mensagemErro to set
	 */
	public void setMensagemErro(boolean mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public DataModel<Entity> getEntities() {
		return entities;
	}

	public void setEntities(DataModel<Entity> entities) {
		this.entities = entities;
	}

/*	public String getLetraPesquisa() {
		return letraPesquisa;
	}

	public void setLetraPesquisa(String letraPesquisa) {
		this.letraPesquisa = letraPesquisa;
	}*/

	public List<Entity> getOriginalResult() {
		return originalResult;
	}

	public void setOriginalResult(List<Entity> originalResult) {
		this.originalResult = originalResult;
	}
	
	public Integer getNumeroPagina() {
		return numeroPagina;
	}

	public void setNumeroPagina(Integer numeroPagina) {
		this.numeroPagina = numeroPagina;
	}

/*	public HtmlDatascroller getScroller() {
		return scroller;
	}

	public void setScroller(HtmlDatascroller scroller) {
		this.scroller = scroller;
	}*/

	public String getFileMessages() {
		return fileMessages;
	}

	public void setFileMessages(String fileMessages) {
		this.fileMessages = fileMessages;
	}

	public Properties getMessageProperties() {
		return messageProperties;
	}

	public void setMessageProperties(Properties messageProperties) {
		this.messageProperties = messageProperties;
	}
	
	public void executeSave() {
		try{
			setEntity(service.insert(entity));
			addInfoMessage(getMessage("SUCCESS_INSERT"));
			setCurrentState(STATE_INSERT);
			setEntity(getNewEntityInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void executeSaveAndEdit() {
		setEntity(service.insert(entity));
		addInfoMessage(getMessage("SUCCESS_INSERT"));
		setCurrentState(STATE_EDIT);
	}
	
	public void executeDelete(){
		service.remove(entity);
		addInfoMessage( getMessage( "SUCCESS_REMOVE" ) );
		setCurrentState( STATE_SEARCH );
		setEntity(getNewEntityInstance());
	}
	
	public void executeUpdate(){
		service.update(entity);
		addInfoMessage( getMessage( "SUCCESS_UPDATE" ) );
		setCurrentState( STATE_SEARCH );
	}
}