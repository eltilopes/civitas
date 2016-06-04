package br.com.civitas.processamento.entity;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_matricula")
public class Matricula implements IEntity {
	
	private static final long serialVersionUID = 7550775310790609268L;
	
	@Id
	@GeneratedValue(generator = "seq_matricula", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_matricula", sequenceName = "seq_matricula", allocationSize = 1)
	@Column(name = "ci_matricula")
	private Long id;
	  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_cargo",nullable=false)
	private Cargo cargo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_secretaria",nullable=false)
	private Secretaria secretaria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_setor",nullable=false)
	private Setor setor ;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cd_vinculo",nullable=false)
	private Vinculo vinculo;
	
	@Column(name = "nr_carga_horaria",nullable = false)
	private int cargaHoraria;

	@Column(name = "nr_matricula",nullable=false, length=9)
	private String numeroMatricula;
	
	@Column(name = "nm_funcionario",nullable=false)
	private String nomeFuncionario;

	@Column(name = "dt_admissao")
	@Temporal(TemporalType.DATE)
	private Date dataAdmissao;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroMatricula() {
		return numeroMatricula;
	}

	public void setNumeroMatricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}

	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	public Vinculo getVinculo() {
		return vinculo;
	}

	public void setVinculo(Vinculo vinculo) {
		this.vinculo = vinculo;
	}

	public int getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(int cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public String getNomeFuncionario() {
		return nomeFuncionario;
	}

	public void setNomeFuncionario(String nomeFuncionario) {
		this.nomeFuncionario = nomeFuncionario;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		// TODO notEmptyFields matriculas
		return null;
	}

}
