import { api, unwrapResponse } from "../../lib/api";
import type { PageResponse } from "../shared/types";
import {
  toPacienteCreatePayload,
  toPacienteProfileViewModel,
  toPacienteRows,
  toPacienteUpdatePayload,
} from "./mappers";
import type {
  PacienteApi,
  PacienteFormValues,
  PacienteListApi,
  PacienteListParams,
  PacienteProfileApi,
  PacienteProfileViewModel,
  PacienteRowViewModel,
} from "./types";

function buildPacientesParams(params: PacienteListParams = {}) {
  return {
    size: 200,
    sort: "nome,asc",
    ...params,
  };
}

export async function listPacientesApi(params: PacienteListParams = {}): Promise<PageResponse<PacienteListApi>> {
  return unwrapResponse(api.get<PageResponse<PacienteListApi>>("/pacientes", { params: buildPacientesParams(params) }));
}

export async function listPacientesRows(params: PacienteListParams = {}): Promise<PacienteRowViewModel[]> {
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
  return unwrapResponse(api.post<PacienteApi, ReturnType<typeof toPacienteCreatePayload>>(
    "/pacientes",
    toPacienteCreatePayload(values),
  ));
}

export async function updatePaciente(id: number, values: PacienteFormValues): Promise<PacienteApi> {
  return unwrapResponse(api.put<PacienteApi, ReturnType<typeof toPacienteUpdatePayload>>(
    `/pacientes/${id}`,
    toPacienteUpdatePayload(values),
  ));
}
