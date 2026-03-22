export type MedicamentoApi = {
  id: number;
  nome: string;
  dosagem: string;
  frequencia: string;
  via: string;
  pacienteId: number;
  consultaId: string;
  medicamentoBaseId?: number | null;
};

export type MedicamentoViewModel = {
  id: number;
  nome: string;
  dosagem: string;
  frequencia: string;
  via: string;
  consultaId: string;
};
