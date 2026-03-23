import type { PageResponse } from "../shared/types";

export type MedicamentoApi = {
  id: number;
  nome: string;
  dosagem: string;
  frequencia: string;
  via: string;
  pacienteId: number;
  consultaId: number;
  medicamentoBaseId?: number | null;
  pacienteNome?: string | null;
};

export type MedicamentoViewModel = {
  id: number;
  nome: string;
  dosagem: string;
  frequencia: string;
  via: string;
  consultaId: number;
  medicamentoBaseId: number | null;
};

export type MedicamentoBaseApi = {
  id: number;
  dcb: string;
  nomeComercial: string;
  principioAtivo: string;
  formaFarmaceutica: string;
  dosagemPadrao: string;
  viaAdministracao: string;
};

export type MedicamentoBaseOptionViewModel = {
  id: number;
  label: string;
  subtitle: string;
  dosagemPadrao: string;
  viaAdministracao: string;
};

export type MedicamentoPrescritoPayload = {
  medicamentoBaseId?: number | null;
  nome?: string | null;
  dosagem: string;
  frequencia: string;
  via: string;
};

export type MedicamentoPageResponse = PageResponse<MedicamentoApi>;