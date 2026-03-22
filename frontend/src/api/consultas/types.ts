import type { ConsultaDTO } from "../../mocks/db/seed";

export type ConsultaApi = ConsultaDTO;

export type ConsultaRowViewModel = {
  id: string;
  dataHoraLabel: string;
  pacienteId: number;
  pacienteNome: string;
  medicoNome: string;
  tipo: ConsultaDTO["tipo"];
  duracaoMinutos: ConsultaDTO["duracaoMinutos"];
  status: ConsultaDTO["status"];
  statusLabel: string;
  statusTone: "success" | "warning" | "danger" | "primary" | "neutral";
};
