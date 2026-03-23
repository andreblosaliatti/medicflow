import { api, unwrapResponse } from "../../lib/api";
import { toExameBaseOption, toExameViewModel } from "./mappers";
import type {
  ExameApi,
  ExameBaseApi,
  ExameBaseOptionViewModel,
  ExamePageResponse,
  ExameSolicitadoPayload,
  ExameViewModel,
  UpdateExameRequest,
} from "./types";
import type { PageResponse } from "../shared/types";

export async function listExamesByPacienteId(pacienteId: number): Promise<ExameViewModel[]> {
  const response = await unwrapResponse(api.get<ExamePageResponse>(`/exames-solicitados/paciente/${pacienteId}`, { params: { size: 100 } }));
  return response.content.map(toExameViewModel);
}

export async function updateExameById({ exameId, ...body }: UpdateExameRequest): Promise<ExameViewModel> {
  const response = await unwrapResponse(api.patch<ExameApi, Omit<UpdateExameRequest, "exameId">>(`/exames-solicitados/${exameId}`, body));
  return toExameViewModel(response);
}

export async function listExamesByConsultaId(consultaId: number): Promise<ExameViewModel[]> {
  const response = await unwrapResponse(api.get<ExamePageResponse>(`/exames-solicitados/consulta/${consultaId}`, { params: { size: 100 } }));
  return response.content.map(toExameViewModel);
}

export async function searchExamesBase(query: string): Promise<ExameBaseOptionViewModel[]> {
  if (!query.trim()) return [];
  const response = await unwrapResponse(api.get<PageResponse<ExameBaseApi>>("/exames-base", {
    params: { nome: query.trim(), size: 20, sort: "nome,asc" },
  }));
  return response.content.map(toExameBaseOption);
}

export async function createExameSolicitado(payload: ExameSolicitadoPayload): Promise<ExameViewModel> {
  const response = await unwrapResponse(api.post<ExameApi, ExameSolicitadoPayload>("/exames-solicitados", payload));
  return toExameViewModel(response);
}

export async function deleteExameSolicitado(exameId: number): Promise<void> {
  await unwrapResponse(api.delete<void>(`/exames-solicitados/${exameId}`));
}
