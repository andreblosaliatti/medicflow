import { api, unwrapResponse } from "../../lib/api";
import { toMedicamentoViewModel } from "./mappers";
import type { MedicamentoApi, MedicamentoViewModel } from "./types";

export async function listMedicamentosByPacienteId(pacienteId: number): Promise<MedicamentoViewModel[]> {
  const response = await unwrapResponse(api.get<MedicamentoApi[]>(`/pacientes/${pacienteId}/medicamentos`));
  return response.map(toMedicamentoViewModel);
}

export async function duplicateMedicamentoById(medicamentoId: number): Promise<MedicamentoViewModel> {
  const response = await unwrapResponse(api.post<MedicamentoApi>(`/medicamentos/${medicamentoId}/duplicate`));
  return toMedicamentoViewModel(response);
}
