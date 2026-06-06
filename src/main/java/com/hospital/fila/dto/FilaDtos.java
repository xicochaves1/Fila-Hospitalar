package com.hospital.fila.dto;

import com.hospital.fila.enums.PrioridadeAtendimento;
import com.hospital.fila.enums.StatusFila;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FilaDtos {

    public static class CheckInRequest {
        public String nomePaciente;
        public String cpf;
        public LocalDate dataNascimento;
        public String telefone;
        public PrioridadeAtendimento prioridade;
        public String especialidade;
        public String observacoes;
    }

    public static class ChamarProximoRequest {
        public String guiche;
        public String especialidade;
    }

    public static class AtualizarStatusRequest {
        public StatusFila novoStatus;
        public String observacoes;
    }

    public static class FilaItemResponse {
        public Long id;
        public String senha;
        public String nomePaciente;
        public PrioridadeAtendimento prioridade;
        public StatusFila status;
        public String especialidade;
        public String guiche;
        public LocalDateTime dataEntrada;
        public LocalDateTime dataInicioAtendimento;
        public int posicaoNaFila;
        public long tempoEsperaMinutos;
    }

    public static class PainelResponse {
        public String senhaAtual;
        public String guicheAtual;
        public long totalAguardando;
        public long totalAtendidos;
        public List<FilaItemResponse> proximos;
    }

    public static class EstatisticasResponse {
        public long totalHoje;
        public long aguardando;
        public long emAtendimento;
        public long atendidos;
        public long ausentes;
        public double tempoMedioEsperaMinutos;
        public Map<String, Long> porPrioridade;
    }
}
