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

    private final FilaService filaService;

    public FilaController(FilaService filaService) {
        this.filaService = filaService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<FilaAtendimento> checkIn(@RequestBody Map<String, String> body) {
        PrioridadeAtendimento prioridade = PrioridadeAtendimento.valueOf(
                body.getOrDefault("prioridade", "NORMAL")
        );
        FilaAtendimento resultado = filaService.checkIn(
                body.get("nomePaciente"),
                body.get("cpf"),
                prioridade,
                body.get("especialidade"),
                body.get("observacoes")
        );
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<List<FilaAtendimento>> getFilaAtual(
            @RequestParam(required = false) String especialidade) {
        List<FilaAtendimento> fila = (especialidade != null)
                ? filaService.getFilaAtualPorEspecialidade(especialidade)
                : filaService.getFilaAtual();
        return ResponseEntity.ok(fila);
    }

    @PostMapping("/chamar")
    public ResponseEntity<?> chamarProximo(@RequestBody Map<String, String> body) {
        return filaService.chamarProximo(body.get("guiche"), body.get("especialidade"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FilaAtendimento> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        StatusFila status = StatusFila.valueOf(body.get("status"));
        FilaAtendimento resultado = filaService.finalizarAtendimento(id, status, body.get("observacoes"));
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/senha/{senha}")
    public ResponseEntity<?> buscarPorSenha(@PathVariable String senha) {
        return filaService.buscarPorSenha(senha)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/historico/paciente/{pacienteId}")
    public ResponseEntity<List<FilaAtendimento>> getHistoricoPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(filaService.getHistoricoPaciente(pacienteId));
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(filaService.getEstatisticas());
    }
}
