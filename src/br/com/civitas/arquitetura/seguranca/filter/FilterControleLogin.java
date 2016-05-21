package br.com.civitas.arquitetura.seguranca.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

public class FilterControleLogin implements Filter {

	private FilterConfig filterConfig;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		HttpServletRequest requestHttp = (HttpServletRequest) request;
		HttpServletResponse responseHttp = (HttpServletResponse) response;
		HttpSession session = requestHttp.getSession();
		
		if (session.getAttribute("usuario") == null){
			responseHttp.sendRedirect( Acesso.getLoginPage() );
			requestHttp.getSession().invalidate();
			return;
		}
		
		if(!permiteAcessoPagina(requestHttp)){
			try{
				String noAcessoPage = Acesso.getNoAccessPage();
				if(StringUtils.isNotBlank(noAcessoPage)){
					responseHttp.sendRedirect( noAcessoPage );
				}else{
					responseHttp.sendRedirect( Acesso.getLoginPage());
					requestHttp.getSession().invalidate();				
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return;
		}
	
		chain.doFilter(request, response);
	}

	public void destroy() {}

	protected FilterConfig getFilterConfig() {
		return filterConfig;
	}

	@SuppressWarnings("unchecked")
	public boolean permiteAcessoPagina( HttpServletRequest request ){
		String pagina = request.getRequestURI();
		String[] dirs = (String[])request.getSession().getAttribute( "dirsNoFilter" );
		//Verifica se está num diretório que não precisar verificar a autenticação.
		if( dirs != null ){
			for( String d : dirs ){
				if( pagina.trim().matches( d.trim() ) ){
					return true;
				}
			}
		}
		String[] pages = (String[])request.getSession().getAttribute( "pagesNoFilter" );
		//Verifica se está numa página que não precisar verificar a autenticação.
		if( pages != null ){
			for( String p : pages ){
				if( pagina.trim().equals( p.trim() ) ){
					return true;
				}
			}
		}		
		
		if( request.getMethod().equals( "GET" ) ){
			Properties pagesSystem = (Properties)request.getSession().getAttribute( "pagesSystem" );
			if( pagesSystem != null ){
				String t = pagesSystem.getProperty( pagina );
				String trans[] = t.split( "," );
				if( trans != null ){
					for( String transacao : trans ){
						if( transacao != null && !transacao.trim().isEmpty()){
							Map<String, Acesso> acesso = (Map<String, Acesso>)request.getSession().getAttribute( "acessos" );
							Acesso a = acesso.get( transacao.trim() );
							if( a != null && a.isPermissao() ){				
								return true;
							}
						}
					}
				}
			}
		}else{
			return true;
		}
		return false;
	}
}
