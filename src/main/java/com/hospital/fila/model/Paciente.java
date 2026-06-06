package com.hospital.fila.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    private String cpf;

    private LocalDate dataNascimento;
    private String telefone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate d) { this.dataNascimento = d; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime d) { this.dataCadastro = d; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Paciente obj = new Paciente();
        public Builder nome(String v) { obj.nome = v; return this; }
        public Builder cpf(String v) { obj.cpf = v; return this; }
        public Builder dataNascimento(LocalDate v) { obj.dataNascimento = v; return this; }
        public Builder telefone(String v) { obj.telefone = v; return this; }
        public Paciente build() { return obj; }
    }
}
