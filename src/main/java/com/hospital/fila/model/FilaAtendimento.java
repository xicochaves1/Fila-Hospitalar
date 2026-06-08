package com.hospital.fila.model;

import com.hospital.fila.enums.PrioridadeAtendimento;
import com.hospital.fila.enums.StatusFila;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fila_atendimento")
public class FilaAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String senha;  // Ex: CG001, OR002, PE001

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    // Nome temporário antes de cadastrar na recepção
    private String nomeTemp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeAtendimento prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFila status;

    private String especialidade;   // Clínico Geral, Ortopedia, etc.
    private String guiche;          // Guichê da recepção
    private String consultorio;     // Consultório do médico

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataEntrada;

    private LocalDateTime dataChegadaRecepcao;
    private LocalDateTime dataEnvioConsultorio;
    private LocalDateTime dataInicioConsulta;
    private LocalDateTime dataFim;

    private String observacoes;

    @PrePersist
    public void prePersist() {
        this.dataEntrada = LocalDateTime.now();
        this.status = StatusFila.AGUARDANDO_RECEPCAO;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public String getNomeTemp() { return nomeTemp; }
    public void setNomeTemp(String nomeTemp) { this.nomeTemp = nomeTemp; }
    public PrioridadeAtendimento getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeAtendimento prioridade) { this.prioridade = prioridade; }
    public StatusFila getStatus() { return status; }
    public void setStatus(StatusFila status) { this.status = status; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getGuiche() { return guiche; }
    public void setGuiche(String guiche) { this.guiche = guiche; }
    public String getConsultorio() { return consultorio; }
    public void setConsultorio(String consultorio) { this.consultorio = consultorio; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime d) { this.dataEntrada = d; }
    public LocalDateTime getDataChegadaRecepcao() { return dataChegadaRecepcao; }
    public void setDataChegadaRecepcao(LocalDateTime d) { this.dataChegadaRecepcao = d; }
    public LocalDateTime getDataEnvioConsultorio() { return dataEnvioConsultorio; }
    public void setDataEnvioConsultorio(LocalDateTime d) { this.dataEnvioConsultorio = d; }
    public LocalDateTime getDataInicioConsulta() { return dataInicioConsulta; }
    public void setDataInicioConsulta(LocalDateTime d) { this.dataInicioConsulta = d; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime d) { this.dataFim = d; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getNomeExibicao() {
        if (paciente != null && paciente.getNome() != null) return paciente.getNome();
        if (nomeTemp != null) return nomeTemp;
        return "Paciente";
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final FilaAtendimento obj = new FilaAtendimento();
        public Builder senha(String v) { obj.senha = v; return this; }
        public Builder paciente(Paciente v) { obj.paciente = v; return this; }
        public Builder nomeTemp(String v) { obj.nomeTemp = v; return this; }
        public Builder prioridade(PrioridadeAtendimento v) { obj.prioridade = v; return this; }
        public Builder status(StatusFila v) { obj.status = v; return this; }
        public Builder especialidade(String v) { obj.especialidade = v; return this; }
        public Builder observacoes(String v) { obj.observacoes = v; return this; }
        public FilaAtendimento build() { return obj; }
    }
}
