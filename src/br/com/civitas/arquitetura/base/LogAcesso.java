package br.com.civitas.arquitetura.base;

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

import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.helpers.utils.DateUtils;

@Entity
@Table(name = "tb_log_acesso")
public class LogAcesso implements IEntity, Comparable<LogAcesso> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "seq_log_acesso", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_log_acesso", sequenceName="seq_log_acesso", allocationSize=1)
	@Column(name = "ci_log_acesso")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_usuario",nullable=false)
	private Usuario usuario;
	
	@Column(name = "dt_acesso", nullable=false)
	private Date dataAcesso;
	
	@Column(name = "ds_ip",nullable=false, length=80)
	private String ip;
	
	@Column(name = "nm_browser",nullable=false, length=250)
	private String browser;
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getDataAcesso() {
		return dataAcesso;
	}

	public void setDataAcesso(Date dataAcesso) {
		this.dataAcesso = dataAcesso;
	}
	
	public String getData(){
		return new DateTime(dataAcesso).toString("dd/MM/yyyy");
	}
	
	public String getHora(){
		return new DateTime(dataAcesso).toString("HH:mm");
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(dataAcesso != null){
			map.put("dataAcesso", DateUtils.dateToString(dataAcesso));
		}if(usuario != null && usuario.getId() != null){
			map.put("usuario.id", usuario.getId());
		}
		return map;
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
		LogAcesso other = (LogAcesso) obj;
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

	@Override
	public int compareTo(LogAcesso o) {
		return getId().compareTo(o.getId().longValue());
	}
}