package com.hospital.fila.repository;

import com.hospital.fila.enums.StatusFila;
import com.hospital.fila.model.FilaAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilaAtendimentoRepository extends JpaRepository<FilaAtendimento, Long> {

    // Fila da recepção (aguardando ser chamado)
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO_RECEPCAO'
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEntrada ASC
    """)
    List<FilaAtendimento> findFilaRecepcao();

    // Fila do consultório por especialidade
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO_CONSULTORIO'
        AND (:especialidade IS NULL OR f.especialidade = :especialidade)
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEnvioConsultorio ASC
    """)
    List<FilaAtendimento> findFilaConsultorio(@Param("especialidade") String especialidade);

    // Próximo na fila da recepção
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO_RECEPCAO'
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
    Optional<FilaAtendimento> findProximoRecepcao();

    // Próximo no consultório
    @Query("""
        SELECT f FROM FilaAtendimento f
        WHERE f.status = 'AGUARDANDO_CONSULTORIO'
        AND f.especialidade = :especialidade
        ORDER BY
            CASE f.prioridade
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                WHEN 'PRIORITARIO' THEN 3
                WHEN 'NORMAL' THEN 4
            END,
            f.dataEnvioConsultorio ASC
        LIMIT 1
    """)
    Optional<FilaAtendimento> findProximoConsultorio(@Param("especialidade") String especialidade);

    Optional<FilaAtendimento> findBySenha(String senha);
    long countByStatus(StatusFila status);

    @Query("SELECT COUNT(f) FROM FilaAtendimento f WHERE f.senha LIKE :prefixo% AND DATE(f.dataEntrada) = CURRENT_DATE")
    long countSenhaHoje(@Param("prefixo") String prefixo);

    @Query("SELECT f FROM FilaAtendimento f WHERE f.paciente.id = :pacienteId ORDER BY f.dataEntrada DESC")
    List<FilaAtendimento> findHistoricoPorPaciente(@Param("pacienteId") Long pacienteId);

    @Query("SELECT f FROM FilaAtendimento f WHERE DATE(f.dataEntrada) = CURRENT_DATE ORDER BY f.dataEntrada DESC")
    List<FilaAtendimento> findHistoricoDia();
}
