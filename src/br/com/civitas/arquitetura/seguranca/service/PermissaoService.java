package br.com.civitas.arquitetura.seguranca.service;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class PermissaoService extends AbstractPersistence<Permissao> {

	private static final long serialVersionUID = 8715007216223535281L;

	@Override
	protected Class<Permissao> getClazz() {
		return Permissao.class;
	}
	
}
