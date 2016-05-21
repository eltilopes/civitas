package br.com.civitas.arquitetura.persistence.jdbc.connection;

import java.sql.Connection;

public interface JDBCConnection {
	
	public Connection getConnection();
	
	public void close();

}
