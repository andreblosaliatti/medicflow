import { api, unwrapResponse } from "../../lib/api";
import { toExameViewModel } from "./mappers";
import type { ExameApi, ExameViewModel, UpdateExameRequest } from "./types";

export async function listExamesByPacienteId(pacienteId: number): Promise<ExameViewModel[]> {
  const response = await unwrapResponse(api.get<ExameApi[]>(`/pacientes/${pacienteId}/exames`));
  return response.map(toExameViewModel);
}

export async function updateExameById({ exameId, ...body }: UpdateExameRequest): Promise<ExameViewModel> {
  const response = await unwrapResponse(api.patch<ExameApi, Omit<UpdateExameRequest, "exameId">>(`/exames/${exameId}`, body));
  return toExameViewModel(response);
}
