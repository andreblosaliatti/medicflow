export type StatusConsulta =
  | "AGENDADA"
  | "CONFIRMADA"
  | "EM_ATENDIMENTO"
  | "CONCLUIDA"
  | "CANCELADA";

export type TipoConsulta =
  | "PRESENCIAL"
  | "TELECONSULTA"
  | "RETORNO";

export type DuracaoMinutos = 15 | 30 | 45 | 60;

export type MeioPagamento = "PIX" | "CARTAO" | "DINHEIRO";