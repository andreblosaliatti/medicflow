import { api, unwrapResponse } from "../../lib/api";
import { toMedicamentoBaseOption, toMedicamentoViewModel } from "./mappers";
import type {
  MedicamentoApi,
  MedicamentoBaseApi,
  MedicamentoBaseOptionViewModel,
  MedicamentoPageResponse,
  MedicamentoPrescritoPayload,
  MedicamentoViewModel,
} from "./types";

export async function listMedicamentosByPacienteId(pacienteId: number): Promise<MedicamentoViewModel[]> {
  const response = await unwrapResponse(api.get<MedicamentoPageResponse>(`/medicamentos/paciente/${pacienteId}`, { params: { size: 100 } }));
  return response.content.map(toMedicamentoViewModel);
}


export async function listMedicamentosByConsultaId(consultaId: number): Promise<MedicamentoViewModel[]> {
  const response = await unwrapResponse(api.get<MedicamentoPageResponse>(`/medicamentos/consultas/${consultaId}`, { params: { size: 100 } }));
  return response.content.map(toMedicamentoViewModel);
}

export async function searchMedicamentosBase(query: string): Promise<MedicamentoBaseOptionViewModel[]> {
  if (!query.trim()) return [];

  const response = await unwrapResponse(
    api.get<MedicamentoBaseApi[]>("/medicamentos-base", {
      params: { q: query.trim() },
    }),
  );

  return response.map(toMedicamentoBaseOption);
}

export async function createMedicamentoPrescrito(consultaId: number, payload: MedicamentoPrescritoPayload): Promise<MedicamentoViewModel> {
  const response = await unwrapResponse(api.post<MedicamentoApi, MedicamentoPrescritoPayload>(`/medicamentos/consultas/${consultaId}`, payload));
  return toMedicamentoViewModel(response);
}

export async function updateMedicamentoPrescrito(medicamentoId: number, payload: MedicamentoPrescritoPayload): Promise<MedicamentoViewModel> {
  const response = await unwrapResponse(api.put<MedicamentoApi, MedicamentoPrescritoPayload>(`/medicamentos/${medicamentoId}`, payload));
  return toMedicamentoViewModel(response);
}

export async function deleteMedicamentoPrescrito(medicamentoId: number): Promise<void> {
  await unwrapResponse(api.delete<void>(`/medicamentos/${medicamentoId}`));
}
