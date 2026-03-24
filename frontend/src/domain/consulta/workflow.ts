import type { StatusConsulta } from "../enums/statusConsulta";

export function canConfirmConsulta(status: StatusConsulta) {
  return status === "AGENDADA";
}

export function canCancelConsulta(status: StatusConsulta) {
  return status === "AGENDADA" || status === "CONFIRMADA";
}

export function canStartConsulta(status: StatusConsulta) {
  return status === "CONFIRMADA";
}

export function canFinishConsulta(status: StatusConsulta) {
  return status === "EM_ATENDIMENTO";
}

export function canEditConsulta(status: StatusConsulta) {
  return status === "AGENDADA" || status === "CONFIRMADA";
}

export function isTerminalConsulta(status: StatusConsulta) {
  return status === "CONCLUIDA" || status === "CANCELADA" || status === "NAO_COMPARECEU";
}
