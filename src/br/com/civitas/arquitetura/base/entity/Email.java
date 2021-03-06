package br.com.civitas.arquitetura.base.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import br.com.civitas.arquitetura.base.enumeration.TipoEmail;
import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_email")
public class Email  implements IEntity {

	private static final long serialVersionUID = -8431221905825030795L;

	@Id
	@GeneratedValue(generator = "seq_email", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_email", sequenceName="seq_email", allocationSize=1)
	@Column(name = "ci_email")
	private Long id;
	
	@Column(name = "dt_envio",nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEnvio;

	@Length(max = 255)
	@Column(name = "ds_titulo",nullable = false)
	private String titulo;

	@Column(name = "ds_corpo",nullable = false)
	private String corpo;

	@Length(max = 255)
	@Column(name = "nm_destinatario",nullable = false)
	private String destinatario;

	@Column(name = "nr_tipo_email",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoEmail tipoEmail;

	@Column(name = "fl_enviado",nullable = false)
	private Boolean enviado;
	
	@Column(name = "fl_reenviado",nullable = false)
	private Integer reenviado;
	
	public Email() {
		super();
		reenviado = 0;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public TipoEmail getTipoEmail() {
		return tipoEmail;
	}

	public void setTipoEmail(TipoEmail tipoEmail) {
		this.tipoEmail = tipoEmail;
	}

	public Boolean getEnviado() {
		return enviado;
	}

	public void setEnviado(Boolean enviado) {
		this.enviado = enviado;
	}

	public Integer getReenviado() {
		return reenviado;
	}

	public void setReenviado(Integer reenviado) {
		this.reenviado = reenviado;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO Auto-generated method stub
		return null;
	}
	
}