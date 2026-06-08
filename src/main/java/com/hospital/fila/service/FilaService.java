package com.hospital.fila.service;

import com.hospital.fila.enums.PrioridadeAtendimento;
import com.hospital.fila.enums.StatusFila;
import com.hospital.fila.model.FilaAtendimento;
import com.hospital.fila.model.Paciente;
import com.hospital.fila.repository.FilaAtendimentoRepository;
import com.hospital.fila.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FilaService {

    private final FilaAtendimentoRepository filaRepo;
    private final PacienteRepository pacienteRepo;

    public FilaService(FilaAtendimentoRepository filaRepo, PacienteRepository pacienteRepo) {
        this.filaRepo = filaRepo;
        this.pacienteRepo = pacienteRepo;
    }

    // ==========================================
    // ETAPA 1: TOTEM — Retirar senha
    // ==========================================
    @Transactional
    public synchronized FilaAtendimento retirarSenha(
            String especialidade,
            PrioridadeAtendimento prioridade,
            String nomeTemp) {

        String senha = gerarSenha(especialidade);
        FilaAtendimento f = FilaAtendimento.builder()
                .senha(senha)
                .especialidade(especialidade)
                .prioridade(prioridade != null ? prioridade : PrioridadeAtendimento.NORMAL)
                .nomeTemp(nomeTemp)
                .build();
        return filaRepo.save(f);
    }

    // ==========================================
    // ETAPA 2A: RECEPÇÃO — Chamar próximo pela senha
    // ==========================================
    @Transactional
    public synchronized Optional<FilaAtendimento> chamarProximoRecepcao(String guiche) {
        Optional<FilaAtendimento> proximo = filaRepo.findProximoRecepcao();
        proximo.ifPresent(f -> {
            f.setStatus(StatusFila.NA_RECEPCAO);
            f.setGuiche(guiche);
            f.setDataChegadaRecepcao(LocalDateTime.now());
            filaRepo.save(f);
        });
        return proximo;
    }

    // ==========================================
    // ETAPA 2B: RECEPÇÃO — Cadastrar paciente e enviar ao consultório
    // ==========================================
    @Transactional
    public FilaAtendimento cadastrarEEnviarConsultorio(
            Long filaId,
            String nomePaciente,
            String cpf,
            String consultorio,
            String observacoes) {

        FilaAtendimento fila = filaRepo.findById(filaId)
                .orElseThrow(() -> new RuntimeException("Fila não encontrada: " + filaId));

        // Busca ou cria paciente
        Paciente paciente;
        if (cpf != null && !cpf.isBlank()) {
            paciente = pacienteRepo.findByCpf(cpf)
                    .orElseGet(() -> pacienteRepo.save(
                            Paciente.builder().nome(nomePaciente).cpf(cpf).build()
                    ));
            paciente.setNome(nomePaciente);
            pacienteRepo.save(paciente);
        } else {
            paciente = pacienteRepo.save(Paciente.builder().nome(nomePaciente).build());
        }

        fila.setPaciente(paciente);
        fila.setConsultorio(consultorio);
        fila.setStatus(StatusFila.AGUARDANDO_CONSULTORIO);
        fila.setDataEnvioConsultorio(LocalDateTime.now());
        if (observacoes != null) fila.setObservacoes(observacoes);

        return filaRepo.save(fila);
    }

    // ==========================================
    // ETAPA 3: CONSULTÓRIO — Médico chama pelo nome
    // ==========================================
    @Transactional
    public synchronized Optional<FilaAtendimento> chamarProximoConsultorio(String especialidade) {
        Optional<FilaAtendimento> proximo = filaRepo.findProximoConsultorio(especialidade);
        proximo.ifPresent(f -> {
            f.setStatus(StatusFila.EM_CONSULTA);
            f.setDataInicioConsulta(LocalDateTime.now());
            filaRepo.save(f);
        });
        return proximo;
    }

    // ==========================================
    // FINALIZAR CONSULTA
    // ==========================================
    @Transactional
    public FilaAtendimento finalizarConsulta(Long filaId, StatusFila status, String obs) {
        FilaAtendimento f = filaRepo.findById(filaId)
                .orElseThrow(() -> new RuntimeException("Não encontrado: " + filaId));
        f.setStatus(status);
        f.setDataFim(LocalDateTime.now());
        if (obs != null) f.setObservacoes(obs);
        return filaRepo.save(f);
    }

    // ==========================================
    // CONSULTAS
    // ==========================================
    public List<FilaAtendimento> getFilaRecepcao() { return filaRepo.findFilaRecepcao(); }
    public List<FilaAtendimento> getFilaConsultorio(String especialidade) { return filaRepo.findFilaConsultorio(especialidade); }
    public Optional<FilaAtendimento> buscarPorSenha(String senha) { return filaRepo.findBySenha(senha); }

    public List<FilaAtendimento> getHistoricoDia() { return filaRepo.findHistoricoDia(); }

    public Map<String, Object> getEstatisticas() {
        Map<String, Object> s = new HashMap<>();
        s.put("aguardandoRecepcao", filaRepo.countByStatus(StatusFila.AGUARDANDO_RECEPCAO));
        s.put("naRecepcao", filaRepo.countByStatus(StatusFila.NA_RECEPCAO));
        s.put("aguardandoConsultorio", filaRepo.countByStatus(StatusFila.AGUARDANDO_CONSULTORIO));
        s.put("emConsulta", filaRepo.countByStatus(StatusFila.EM_CONSULTA));
        s.put("atendidos", filaRepo.countByStatus(StatusFila.ATENDIDO));
        s.put("total", filaRepo.count());
        return s;
    }

    // ==========================================
    // GERAR SENHA por especialidade
    // CG001, OR001, PE001, CA001, EM001
    // ==========================================
    private String gerarSenha(String especialidade) {
        String prefixo = switch (especialidade != null ? especialidade : "") {
            case "Ortopedia" -> "OR";
            case "Pediatria" -> "PE";
            case "Cardiologia" -> "CA";
            case "Emergência" -> "EM";
            default -> "CG"; // Clínico Geral
        };
        long count = filaRepo.countSenhaHoje(prefixo) + 1;
        return String.format("%s%03d", prefixo, count);
    }
}
