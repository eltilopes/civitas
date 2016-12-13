package br.com.civitasTest.testConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class JpaTestConfig extends DataSourceTestConfig {

	private static String PERSISTENCE_UNIT_NAME = "civitas_teste";
	
	@Bean
	public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setShowSql(true);
		jpaVendorAdapter.setGenerateDdl(false);
		jpaVendorAdapter.setDatabasePlatform(environment.getProperty("db.dialect"));
		return jpaVendorAdapter;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter());
		entityManagerFactory.setDataSource(remoteDataSource());
		entityManagerFactory.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		return entityManagerFactory;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		return jpaTransactionManager;
	}
	
}
