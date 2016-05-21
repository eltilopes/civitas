package br.com.civitas.arquitetura.entity;

import java.io.Serializable;
import java.util.Map;

public interface Entidade extends Serializable{
	Map<String, Object> notEmptyFields();
}