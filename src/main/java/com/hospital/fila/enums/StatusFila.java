package com.hospital.fila.enums;

public enum StatusFila {
    AGUARDANDO_RECEPCAO,   // Retirou senha, aguarda ser chamado pela recepção
    NA_RECEPCAO,           // Está sendo atendido na recepção
    AGUARDANDO_CONSULTORIO, // Recepção enviou para fila do consultório
    EM_CONSULTA,           // Médico chamou, está em consulta
    ATENDIDO,              // Consulta finalizada
    AUSENTE,               // Não compareceu
    CANCELADO
}
