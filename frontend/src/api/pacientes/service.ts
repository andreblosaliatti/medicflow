import { api, unwrapResponse } from "../../lib/api";
import {
  toPacienteCreatePayload,
  toPacienteProfileViewModel,
  toPacienteRows,
  toPacienteUpdatePayload,
} from "./mappers";
import type {
  PacienteApi,
  PacienteFormValues,
  PacienteListParams,
  PacienteListResponse,
  PacienteProfileApi,
  PacienteProfileViewModel,
  PacienteRowsPageViewModel,
} from "./types";

function buildPacientesParams(params: PacienteListParams = {}): PacienteListParams {
  return {
    page: 0,
    size: 200,
    sort: "nome,asc",
    ...params,
  };
}

export async function listPacientesApi(params: PacienteListParams = {}): Promise<PacienteListResponse> {
  return unwrapResponse(api.get<PacienteListResponse>("/pacientes", { params: buildPacientesParams(params) }));
}

export async function listPacientesRows(params: PacienteListParams = {}): Promise<PacienteRowsPageViewModel> {
  const response = await listPacientesApi(params);
  return toPacienteRows(response);
}

export async function getPacienteById(id: number): Promise<PacienteApi> {
  return unwrapResponse(api.get<PacienteApi>(`/pacientes/${id}`));
}

export async function getPacienteProfileById(id: number): Promise<PacienteProfileViewModel> {
  const response = await unwrapResponse(api.get<PacienteProfileApi>(`/pacientes/${id}/perfil`));
  return toPacienteProfileViewModel(response);
}

export async function createPaciente(values: PacienteFormValues): Promise<PacienteApi> {
  const payload = toPacienteCreatePayload(values);
  return unwrapResponse(api.post<PacienteApi, typeof payload>("/pacientes", payload));
}

export async function updatePaciente(id: number, values: PacienteFormValues): Promise<PacienteApi> {
  const payload = toPacienteUpdatePayload(values);
  return unwrapResponse(api.put<PacienteApi, typeof payload>(`/pacientes/${id}`, payload));
}
