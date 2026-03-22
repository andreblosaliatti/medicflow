export type StatusExameApi = "SOLICITADO" | "AGENDADO" | "REALIZADO" | "CANCELADO";

export type ExameApi = {
  id: number;
  status: StatusExameApi;
  justificativa: string;
  observacoes: string;
  dataColeta: string | null;
  dataResultado: string | null;
  consultaId: string;
  pacienteId: number;
  exameBaseId?: number | null;
  nome: string;
};

export type ExameViewModel = ExameApi;

export type UpdateExameRequest = Partial<Pick<ExameApi, "status" | "dataColeta" | "dataResultado" | "observacoes">> & {
  exameId: number;
};
