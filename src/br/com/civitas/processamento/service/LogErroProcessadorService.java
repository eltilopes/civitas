package br.com.civitas.processamento.service;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.LogErroProcessador;

@Service
public class LogErroProcessadorService extends AbstractPersistence<LogErroProcessador> {

	private static final long serialVersionUID = -9088302441771274508L;

	@Override
	protected Class<LogErroProcessador> getClazz() {
		return LogErroProcessador.class;
	}
	
}
