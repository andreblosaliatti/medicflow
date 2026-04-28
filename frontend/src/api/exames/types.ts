import type { PageResponse } from "../shared/types";

export type StatusExameApi = "SOLICITADO" | "AGENDADO" | "REALIZADO" | "CANCELADO";

export type ExameApi = {
  id: number;
  status: StatusExameApi;
  justificativa: string;
  observacoes: string;
  dataColeta: string | null;
  dataResultado: string | null;
  consultaId: number;
  pacienteId: number;
  exameBaseId?: number | null;
  nome?: string | null;
  exameNome?: string | null;
};

export type ExameViewModel = {
  id: number;
  status: StatusExameApi;
  justificativa: string;
  observacoes: string;
  dataColeta: string | null;
  dataResultado: string | null;
  consultaId: number;
  pacienteId: number;
  exameBaseId: number | null;
  nome: string;
};

export type UpdateExameRequest = Partial<Pick<ExameApi, "status" | "dataColeta" | "dataResultado" | "observacoes">> & {
  exameId: number;
};

export type ExameBaseApi = {
  id: number;
  nome: string;
  codigoTuss: string;
  tipo: string;
  prazoEstimado: number;
};

export type ExameBaseOptionViewModel = {
  id: number;
  label: string;
  subtitle: string;
};

export type ExameSolicitadoPayload = {
  consultaId: number;
  exameBaseId: number;
  status?: StatusExameApi;
  justificativa?: string;
  observacoes?: string;
  dataColeta?: string | null;
  dataResultado?: string | null;
};

export type ExamePageResponse = PageResponse<ExameApi>;
