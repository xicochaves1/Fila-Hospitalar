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
    private String senha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeAtendimento prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFila status;

    private String guiche;
    private String especialidade;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataEntrada;

    private LocalDateTime dataInicioAtendimento;
    private LocalDateTime dataFimAtendimento;
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendente_id")
    private Usuario atendente;

    @PrePersist
    public void prePersist() {
        this.dataEntrada = LocalDateTime.now();
        this.status = StatusFila.AGUARDANDO;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public PrioridadeAtendimento getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeAtendimento prioridade) { this.prioridade = prioridade; }

    public StatusFila getStatus() { return status; }
    public void setStatus(StatusFila status) { this.status = status; }

    public String getGuiche() { return guiche; }
    public void setGuiche(String guiche) { this.guiche = guiche; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataInicioAtendimento() { return dataInicioAtendimento; }
    public void setDataInicioAtendimento(LocalDateTime d) { this.dataInicioAtendimento = d; }

    public LocalDateTime getDataFimAtendimento() { return dataFimAtendimento; }
    public void setDataFimAtendimento(LocalDateTime d) { this.dataFimAtendimento = d; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Usuario getAtendente() { return atendente; }
    public void setAtendente(Usuario atendente) { this.atendente = atendente; }

    // Builder manual
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final FilaAtendimento obj = new FilaAtendimento();
        public Builder senha(String v) { obj.senha = v; return this; }
        public Builder paciente(Paciente v) { obj.paciente = v; return this; }
        public Builder prioridade(PrioridadeAtendimento v) { obj.prioridade = v; return this; }
        public Builder status(StatusFila v) { obj.status = v; return this; }
        public Builder guiche(String v) { obj.guiche = v; return this; }
        public Builder especialidade(String v) { obj.especialidade = v; return this; }
        public Builder observacoes(String v) { obj.observacoes = v; return this; }
        public FilaAtendimento build() { return obj; }
    }
}
