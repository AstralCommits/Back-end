package br.com.trama.model.enums;

/** Status do pedido, conforme o CHECK chk_ped_status da tabela pedido. */
public enum StatusPedido {
    PENDENTE,
    EM_TRANSITO,
    ENTREGUE,
    CANCELADO
}
