package com.hospital.fila.service;

import com.hospital.fila.enums.PrioridadeAtendimento;
import com.hospital.fila.enums.StatusFila;
import com.hospital.fila.model.FilaAtendimento;
import com.hospital.fila.model.Paciente;
import com.hospital.fila.repository.FilaAtendimentoRepository;
import com.hospital.fila.repository.PacienteRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FilaService {

    private final FilaAtendimentoRepository filaRepository;
    private final PacienteRepository pacienteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public FilaService(FilaAtendimentoRepository filaRepository,
                       PacienteRepository pacienteRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.filaRepository = filaRepository;
        this.pacienteRepository = pacienteRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public synchronized FilaAtendimento checkIn(String nomePaciente, String cpf,
            PrioridadeAtendimento prioridade, String especialidade, String observacoes) {

        Paciente paciente;
        if (cpf != null && !cpf.isBlank()) {
            paciente = pacienteRepository.findByCpf(cpf)
                    .orElseGet(() -> pacienteRepository.save(
                            Paciente.builder().nome(nomePaciente).cpf(cpf).build()
                    ));
        } else {
            paciente = pacienteRepository.save(Paciente.builder().nome(nomePaciente).build());
        }

        String senha = gerarSenha(prioridade);

        FilaAtendimento entrada = FilaAtendimento.builder()
                .senha(senha)
                .paciente(paciente)
                .prioridade(prioridade)
                .status(StatusFila.AGUARDANDO)
                .especialidade(especialidade != null ? especialidade : "Clínico Geral")
                .observacoes(observacoes)
                .build();

        FilaAtendimento salvo = filaRepository.save(entrada);
        notificarAtualizacao("CHECK_IN", salvo);
        return salvo;
    }

    @Transactional
    public synchronized Optional<FilaAtendimento> chamarProximo(String guiche, String especialidade) {
        Optional<FilaAtendimento> proximo = (especialidade != null && !especialidade.isBlank())
                ? filaRepository.findProximoPorEspecialidade(especialidade)
                : filaRepository.findProximo();

        proximo.ifPresent(f -> {
            f.setStatus(StatusFila.EM_ATENDIMENTO);
            f.setGuiche(guiche);
            f.setDataInicioAtendimento(LocalDateTime.now());
            filaRepository.save(f);
            notificarAtualizacao("CHAMAR", f);
        });

        return proximo;
    }

    @Transactional
    public FilaAtendimento finalizarAtendimento(Long filaId, StatusFila novoStatus, String observacoes) {
        FilaAtendimento fila = filaRepository.findById(filaId)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado: " + filaId));
        fila.setStatus(novoStatus);
        fila.setDataFimAtendimento(LocalDateTime.now());
        if (observacoes != null) fila.setObservacoes(observacoes);
        FilaAtendimento salvo = filaRepository.save(fila);
        notificarAtualizacao("FINALIZAR", salvo);
        return salvo;
    }

    public List<FilaAtendimento> getFilaAtual() {
        return filaRepository.findFilaAtual();
    }

    public List<FilaAtendimento> getFilaAtualPorEspecialidade(String especialidade) {
        return filaRepository.findFilaPorEspecialidade(especialidade);
    }

    public Optional<FilaAtendimento> buscarPorSenha(String senha) {
        return filaRepository.findBySenha(senha);
    }

    public List<FilaAtendimento> getHistoricoPaciente(Long pacienteId) {
        return filaRepository.findHistoricoPorPaciente(pacienteId);
    }

    public Map<String, Object> getEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("aguardando", filaRepository.countByStatus(StatusFila.AGUARDANDO));
        stats.put("emAtendimento", filaRepository.countByStatus(StatusFila.EM_ATENDIMENTO));
        stats.put("atendidos", filaRepository.countByStatus(StatusFila.ATENDIDO));
        stats.put("ausentes", filaRepository.countByStatus(StatusFila.AUSENTE));

        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<FilaAtendimento> atendidosHoje = filaRepository
                .findByStatusAndDataEntradaBetween(StatusFila.ATENDIDO, inicioDia, LocalDateTime.now());

        double tempoMedio = atendidosHoje.stream()
                .filter(f -> f.getDataInicioAtendimento() != null)
                .mapToLong(f -> ChronoUnit.MINUTES.between(f.getDataEntrada(), f.getDataInicioAtendimento()))
                .average().orElse(0);

        stats.put("tempoMedioEsperaMinutos", Math.round(tempoMedio));
        stats.put("totalHoje", atendidosHoje.size() + (long) stats.get("aguardando") + (long) stats.get("emAtendimento"));
        return stats;
    }

    private String gerarSenha(PrioridadeAtendimento prioridade) {
        String prefixo = switch (prioridade) {
            case EMERGENCIA -> "E";
            case URGENTE -> "U";
            case PRIORITARIO -> "P";
            case NORMAL -> "N";
        };
        long count = filaRepository.countSenhaHoje(prefixo) + 1;
        return String.format("%s%03d", prefixo, count);
    }

    private void notificarAtualizacao(String evento, FilaAtendimento fila) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("evento", evento);
        payload.put("senha", fila.getSenha());
        payload.put("guiche", fila.getGuiche());
        payload.put("prioridade", fila.getPrioridade());
        payload.put("status", fila.getStatus());
        payload.put("totalAguardando", filaRepository.countByStatus(StatusFila.AGUARDANDO));
        messagingTemplate.convertAndSend("/topic/fila", payload);
    }
}
