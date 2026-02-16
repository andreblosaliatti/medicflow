import type { StatusConsulta } from "../enums/statusConsulta";

export function statusLabel(status: StatusConsulta): string {
  switch (status) {
    case "AGENDADA":
      return "Agendada";
    case "CONFIRMADA":
      return "Confirmada";
    case "EM_ATENDIMENTO":
      return "Em atendimento";
    case "CONCLUIDA":
      return "Concluída";
    case "CANCELADA":
      return "Cancelada";
  }
}