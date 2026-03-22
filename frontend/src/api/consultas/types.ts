export type ConsultaApi = {
  id: number;
  dataHora: string;
  tipo: string;
  status: string;
  meioPagamento: string | null;
  valorConsulta: number | null;
  pago: boolean | null;
  duracaoMinutos: number | null;
  pacienteId: number | null;
  pacienteNome: string | null;
  medicoId: number | null;
  medicoNome: string | null;
  motivo: string | null;
};

export type ConsultaTableItemApi = ConsultaApi;

export type ConsultaRowViewModel = {
  id: string;
  dataHoraLabel: string;
  pacienteId: number;
  pacienteNome: string;
  medicoNome: string;
  tipo: string;
  duracaoMinutos: number;
  status: string;
  statusLabel: string;
  statusTone: "success" | "warning" | "danger" | "primary" | "neutral";
};

export type ConsultaHistoryRowViewModel = {
  id: string;
  data: string;
  hora: string;
  profissional: string;
  status: "AGENDADA" | "CONFIRMADA" | "EM_ATENDIMENTO" | "CONCLUIDA" | "CANCELADA";
};
