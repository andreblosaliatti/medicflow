import type { MeioPagamento, StatusConsulta, TipoConsulta } from "../../domain/enums/statusConsulta";

export type EnumOptionApi = {
  code: string;
  label: string;
};

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

export type ConsultaDetailsApi = {
  id: number;
  dataHora: string;
  tipo: string;
  status: string;
  valorConsulta: number | null;
  meioPagamento: string | null;
  pago: boolean | null;
  dataPagamento: string | null;
  duracaoMinutos: number | null;
  retorno: boolean;
  dataLimiteRetorno: string | null;
  linkAcesso: string | null;
  planoSaude: string | null;
  numeroCarteirinha: string | null;
  motivo: string | null;
  anamnese: string | null;
  exameFisico: string | null;
  diagnostico: string | null;
  prescricao: string | null;
  observacoes: string | null;
  pacienteId: number | null;
  pacienteNome: string | null;
  medicoId: number | null;
  medicoNome: string | null;
};

export type ConsultaAgendaItemApi = {
  id: number;
  dataHora: string;
  duracaoMinutos: number | null;
  tipo: string;
  status: string;
  linkAcesso: string | null;
  pacienteId: number | null;
  pacienteNome: string | null;
  medicoId: number | null;
  medicoNome: string | null;
  motivo: string | null;
};

export type ConsultaMetadataApi = {
  status: EnumOptionApi[];
  tipos: EnumOptionApi[];
  meiosPagamento: EnumOptionApi[];
};

export type ConsultaListParams = {
  pacienteId?: number;
  medicoId?: number;
  status?: StatusConsulta;
  tipo?: TipoConsulta;
  meioPagamento?: MeioPagamento;
  pago?: boolean;
  retorno?: boolean;
  termo?: string;
  dataHoraInicio?: string;
  dataHoraFim?: string;
  page?: number;
  size?: number;
  sort?: string;
};

export type ConsultaCreatePayload = {
  dataHora: string;
  tipo: TipoConsulta;
  status: StatusConsulta;
  valorConsulta: number;
  meioPagamento: MeioPagamento;
  pago: boolean;
  dataPagamento: string | null;
  duracaoMinutos: number;
  retorno: boolean;
  dataLimiteRetorno: string | null;
  linkAcesso: string | null;
  planoSaude: string | null;
  numeroCarteirinha: string | null;
  motivo: string;
  anamnese: string | null;
  exameFisico: string | null;
  diagnostico: string | null;
  prescricao: string | null;
  observacoes: string | null;
  pacienteId: number;
  medicoId: number;
};

export type ConsultaUpdatePayload = Partial<ConsultaCreatePayload>;

export type ConsultaRowViewModel = {
  id: string;
  dataHoraLabel: string;
  pacienteId: number;
  pacienteNome: string;
  medicoId: number | null;
  medicoNome: string;
  tipo: TipoConsulta;
  duracaoMinutos: number;
  status: StatusConsulta;
  statusLabel: string;
  statusTone: "success" | "warning" | "danger" | "primary" | "neutral";
};

export type ConsultaHistoryRowViewModel = {
  id: string;
  data: string;
  hora: string;
  profissional: string;
  status: StatusConsulta;
};

export type ConsultaDetailsViewModel = {
  id: string;
  dataHora: string;
  dataHoraLabel: string;
  pacienteId: number;
  pacienteNome: string;
  medicoId: number | null;
  medicoNome: string;
  tipo: TipoConsulta;
  duracaoMinutos: number;
  status: StatusConsulta;
  statusLabel: string;
  statusTone: "success" | "warning" | "danger" | "primary" | "neutral";
  motivo: string;
  sala: string | null;
  telefoneContato: string | null;
  valorConsulta: number;
  valorConsultaLabel: string;
  pago: boolean;
  pagoLabel: string;
  meioPagamento: MeioPagamento | null;
  meioPagamentoLabel: string;
  dataPagamento: string | null;
  teleconsulta: boolean;
  linkAcesso: string | null;
  retorno: boolean;
  dataLimiteRetorno: string | null;
  planoSaude: string | null;
  numeroCarteirinha: string | null;
  anamnese: string | null;
  exameFisico: string | null;
  diagnostico: string | null;
  prescricao: string | null;
  observacoes: string | null;
};

export type AppointmentEventViewModel = {
  id: string;
  patientId: number | null;
  patientName: string;
  professionalId: number | null;
  professionalName: string;
  type: TipoConsulta;
  status: StatusConsulta;
  start: Date;
  end: Date;
  notes?: string;
  room?: string;
  phone?: string;
  linkAcesso?: string | null;
};

export type ConsultaTodayItemViewModel = {
  id: string;
  hora: string;
  paciente: string;
  profissional: string;
  status: string;
};


export type OperationalPendingPriority = "ALTA" | "MEDIA" | "BAIXA";

export type OperationalPendingItemViewModel = ConsultaRowViewModel & {
  pendenciaLabel: string;
  prioridade: OperationalPendingPriority;
  prioridadeTone: "danger" | "warn" | "muted";
};
