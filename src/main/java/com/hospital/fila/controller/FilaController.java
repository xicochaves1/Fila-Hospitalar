package com.hospital.fila.controller;

import com.hospital.fila.enums.PrioridadeAtendimento;
import com.hospital.fila.enums.StatusFila;
import com.hospital.fila.model.FilaAtendimento;
import com.hospital.fila.service.FilaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fila")
@CrossOrigin(origins = "*")
public class FilaController {

    private final FilaService service;
    public FilaController(FilaService service) { this.service = service; }

    // ETAPA 1: Totem — retirar senha
    @PostMapping("/senha")
    public ResponseEntity<FilaAtendimento> retirarSenha(@RequestBody Map<String, String> body) {
        PrioridadeAtendimento prioridade = body.containsKey("prioridade")
                ? PrioridadeAtendimento.valueOf(body.get("prioridade"))
                : PrioridadeAtendimento.NORMAL;
        FilaAtendimento f = service.retirarSenha(
                body.getOrDefault("especialidade", "Clínico Geral"),
                prioridade,
                body.get("nomeTemp")
        );
        return ResponseEntity.ok(f);
    }

    // ETAPA 2A: Recepção — chamar próximo
    @PostMapping("/recepcao/chamar")
    public ResponseEntity<?> chamarProximoRecepcao(@RequestBody Map<String, String> body) {
        return service.chamarProximoRecepcao(body.getOrDefault("guiche", "Guichê 01"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // ETAPA 2B: Recepção — cadastrar e enviar ao consultório
    @PutMapping("/{id}/consultorio")
    public ResponseEntity<FilaAtendimento> enviarConsultorio(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        FilaAtendimento f = service.cadastrarEEnviarConsultorio(
                id,
                body.get("nomePaciente"),
                body.get("cpf"),
                body.get("consultorio"),
                body.get("observacoes")
        );
        return ResponseEntity.ok(f);
    }

    // ETAPA 3: Consultório — médico chama pelo nome
    @PostMapping("/consultorio/chamar")
    public ResponseEntity<?> chamarProximoConsultorio(@RequestBody Map<String, String> body) {
        return service.chamarProximoConsultorio(body.get("especialidade"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Finalizar consulta
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<FilaAtendimento> finalizar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        StatusFila status = StatusFila.valueOf(body.getOrDefault("status", "ATENDIDO"));
        FilaAtendimento f = service.finalizarConsulta(id, status, body.get("observacoes"));
        return ResponseEntity.ok(f);
    }

    // Fila da recepção
    @GetMapping("/recepcao")
    public ResponseEntity<List<FilaAtendimento>> getFilaRecepcao() {
        return ResponseEntity.ok(service.getFilaRecepcao());
    }

    // Fila do consultório
    @GetMapping("/consultorio")
    public ResponseEntity<List<FilaAtendimento>> getFilaConsultorio(
            @RequestParam(required = false) String especialidade) {
        return ResponseEntity.ok(service.getFilaConsultorio(especialidade));
    }

    // Buscar por senha
    @GetMapping("/senha/{senha}")
    public ResponseEntity<?> buscarPorSenha(@PathVariable String senha) {
        return service.buscarPorSenha(senha)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Estatísticas
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(service.getEstatisticas());
    }

    // Histórico do dia
    @GetMapping("/historico")
    public ResponseEntity<List<FilaAtendimento>> getHistorico() {
        return ResponseEntity.ok(service.getHistoricoDia());
    }
}
