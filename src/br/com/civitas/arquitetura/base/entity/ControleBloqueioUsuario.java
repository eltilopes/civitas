package br.com.civitas.arquitetura.base.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.DateTime;

import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_controle_bloqueio_usuario")
public class ControleBloqueioUsuario implements IEntity {

	private static final long serialVersionUID = 8726842549960824533L;

	@Id
	@GeneratedValue(generator = "seq_controle_bloqueio_usuario", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_controle_bloqueio_usuario", sequenceName="seq_controle_bloqueio_usuario", allocationSize=1)
	@Column(name = "ci_controle_bloqueio_usuario")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cd_usuario", nullable = false)
	private Usuario usuario;

	@Column(name="nr_tentativa",nullable = false)
	private Integer tentativa;

	@Column(name = "dt_bloqueio", nullable = false)
	private Date dataBloqueio;

	@Column(name="fl_bloqueado",nullable = false)
	private Boolean bloqueado;
	
	@Column(name="fl_login_sucesso")
	private Boolean loginSucesso;
	
	public ControleBloqueioUsuario() {
		super();
	}
	
	public ControleBloqueioUsuario(Usuario usuario) {
		super();
		this.usuario = usuario;
		this.dataBloqueio = new DateTime().toDate();
		this.bloqueado = Boolean.FALSE;
		this.tentativa = 1;
		this.loginSucesso = Boolean.FALSE;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Integer getTentativa() {
		return tentativa;
	}

	public void setTentativa(Integer tentativa) {
		this.tentativa = tentativa;
	}

	public Date getDataBloqueio() {
		return dataBloqueio;
	}

	public void setDataBloqueio(Date dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}

	public Boolean getBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	public Boolean getLoginSucesso() {
		return loginSucesso;
	}

	public void setLoginSucesso(Boolean loginSucesso) {
		this.loginSucesso = loginSucesso;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}return map;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControleBloqueioUsuario other = (ControleBloqueioUsuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}