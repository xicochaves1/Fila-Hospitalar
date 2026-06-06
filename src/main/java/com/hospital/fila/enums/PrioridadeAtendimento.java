package com.hospital.fila.enums;

/**
 * Prioridade do paciente na fila.
 * Conceito de SD: ordenação distribuída de recursos compartilhados.
 */
public enum PrioridadeAtendimento {
    EMERGENCIA(1, "Emergência"),
    URGENTE(2, "Urgente"),
    PRIORITARIO(3, "Prioritário"),   // idosos, grávidas, deficientes
    NORMAL(4, "Normal");

    private final int ordem;
    private final String descricao;

    PrioridadeAtendimento(int ordem, String descricao) {
        this.ordem = ordem;
        this.descricao = descricao;
    }

    public int getOrdem() { return ordem; }
    public String getDescricao() { return descricao; }
}
