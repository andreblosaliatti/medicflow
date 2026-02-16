import type { StatusConsulta } from "../enums/statusConsulta";

export function statusTone(status: StatusConsulta) {
  switch (status) {
    case "CONFIRMADA":
      return "success";
    case "EM_ATENDIMENTO":
      return "primary";
    case "AGENDADA":
      return "warning";
    case "CONCLUIDA":
      return "neutral";
    case "CANCELADA":
      return "danger";
  }
}