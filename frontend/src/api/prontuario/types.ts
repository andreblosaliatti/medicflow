import type { StatusExameApi } from "../exames/types";

export type ProntuarioDiagnostico = {
  codigo?: string;
  descricao: string;
};

export type ProntuarioMedicacao = {
  nome: string;
  dose?: string;
  posologia: string;
  duracao?: string;
};

export type ProntuarioExame = {
  nome: string;
  status: StatusExameApi;
};

export type ProntuarioConsulta = {
  id: string;
  dataHora: string;
  medicoNome: string;
  motivo: string;
  anamnese?: string;
  exameFisico?: string;
  diagnosticos: ProntuarioDiagnostico[];
  medicacoes: ProntuarioMedicacao[];
  exames: ProntuarioExame[];
  observacoes?: string;
};

export type ProntuarioPaciente = {
  pacienteId: number;
  pacienteNome: string;
  documento?: string;
  dataNascimento?: string;
  consultas: ProntuarioConsulta[];
};
