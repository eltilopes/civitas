package br.com.civitasTest.testConfig;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceTestConfig {

	@Autowired
	protected Environment environment;

	@Bean
    public DataSource remoteDataSource() {
    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
    	dataSource.setDriverClassName(environment.getProperty("db.driver"));
    	dataSource.setUrl(environment.getProperty("db.url"));
    	dataSource.setPassword(environment.getProperty("db.password"));
    	dataSource.setUsername(environment.getProperty("db.username"));
        return dataSource;
    }
}
