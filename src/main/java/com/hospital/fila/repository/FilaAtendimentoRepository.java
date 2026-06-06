package com.hospital.fila.repository;

import com.hospital.fila.enums.PrioridadeAtendimento;
import com.hospital.fila.enums.StatusFila;
import com.hospital.fila.model.FilaAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilaAtendimentoRepository extends JpaRepository<FilaAtendimento, Long> {

    // Fila atual ordenada por prioridade e tempo de entrada
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO'
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEntrada ASC
    """)
    List<FilaAtendimento> findFilaAtual();

    // Fila por especialidade
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO'
        AND (:especialidade IS NULL OR f.especialidade = :especialidade)
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEntrada ASC
    """)
    List<FilaAtendimento> findFilaPorEspecialidade(@Param("especialidade") String especialidade);

    // Próximo da fila (primeiro por prioridade)
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO'
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEntrada ASC
        LIMIT 1
    """)
    Optional<FilaAtendimento> findProximo();

    // Próximo por especialidade
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO'
        AND f.especialidade = :especialidade
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEntrada ASC
        LIMIT 1
    """)
    Optional<FilaAtendimento> findProximoPorEspecialidade(@Param("especialidade") String especialidade);

    // Histórico por período
    List<FilaAtendimento> findByStatusAndDataEntradaBetween(
        StatusFila status, LocalDateTime inicio, LocalDateTime fim
    );

    // Buscar senha
    Optional<FilaAtendimento> findBySenha(String senha);

    // Contar aguardando
    long countByStatus(StatusFila status);

    // Buscar por paciente
    @Query("SELECT f FROM FilaAtendimento f WHERE f.paciente.id = :pacienteId ORDER BY f.dataEntrada DESC")
    List<FilaAtendimento> findHistoricoPorPaciente(@Param("pacienteId") Long pacienteId);

    // Gerar próxima senha por prefixo
    @Query("SELECT COUNT(f) FROM FilaAtendimento f WHERE f.senha LIKE :prefixo% AND DATE(f.dataEntrada) = CURRENT_DATE")
    long countSenhaHoje(@Param("prefixo") String prefixo);
}
