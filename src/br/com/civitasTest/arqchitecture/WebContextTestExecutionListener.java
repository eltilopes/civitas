/**
File: WebContextTestExecutionListener.java

Created at: May 7, 2010

Author: Tomas de Priede

Project: jsf-spring-integration
 */
package br.com.civitasTest.arqchitecture;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * This execution listener will register the request and session scope
 * in the test context of spring.
 * 
 * This way spring will not throw the exception with a message saying:
 * No scope session (or request) register
 * 
 * But check that we are not emulating the real logic of a session or request
 * scope.
 * 
 * 
 * @author Tomas de Priede
 * @since v1.0.0
 *
 */
public class WebContextTestExecutionListener extends AbstractTestExecutionListener {

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {

		if (testContext.getApplicationContext() instanceof GenericApplicationContext) {
			GenericApplicationContext context = (GenericApplicationContext) testContext.getApplicationContext();
			ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
			Scope requestScope = new SimpleThreadScope();
			beanFactory.registerScope("request", requestScope);
			Scope sessionScope = new SimpleThreadScope();
			beanFactory.registerScope("session", sessionScope);
		}
	}
}