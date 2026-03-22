import { httpClient } from "../http";
import { toExameViewModel } from "./mappers";
import type { ExameApi, ExameViewModel, UpdateExameRequest } from "./types";

export async function listExamesByPacienteId(pacienteId: number): Promise<ExameViewModel[]> {
  const response = await httpClient.get<ExameApi[]>(`/pacientes/${pacienteId}/exames`);
  return response.map(toExameViewModel);
}

export async function updateExameById({ exameId, ...body }: UpdateExameRequest): Promise<ExameViewModel> {
  const response = await httpClient.patch<ExameApi, Omit<UpdateExameRequest, "exameId">>(`/exames/${exameId}`, body);
  return toExameViewModel(response);
}
