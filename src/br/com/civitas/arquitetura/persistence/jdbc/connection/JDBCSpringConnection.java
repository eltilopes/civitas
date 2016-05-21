package br.com.civitas.arquitetura.persistence.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class JDBCSpringConnection implements JDBCConnection{

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			return null;
		}
	}

	public void close() {
		try {
			dataSource.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

}
