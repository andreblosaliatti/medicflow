import { httpClient } from "../http";
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

function buildPacientesQuery(params: PacienteListParams = {}) {
  const searchParams = new URLSearchParams();

  if (params.nome?.trim()) searchParams.set("nome", params.nome.trim());
  if (params.cpf?.trim()) searchParams.set("cpf", params.cpf.trim());
  if (typeof params.ativo === "boolean") searchParams.set("ativo", String(params.ativo));
  if (params.convenio?.trim()) searchParams.set("convenio", params.convenio.trim());
  if (typeof params.page === "number") searchParams.set("page", String(params.page));
  if (typeof params.size === "number") searchParams.set("size", String(params.size));
  if (params.sort?.trim()) searchParams.set("sort", params.sort.trim());

  const query = searchParams.toString();
  return query ? `/pacientes?${query}` : "/pacientes";
}

export async function listPacientesApi(params: PacienteListParams = {}): Promise<PageResponse<PacienteListApi>> {
  return httpClient.get<PageResponse<PacienteListApi>>(buildPacientesQuery(params));
}

export async function listPacientesRows(params: PacienteListParams = {}): Promise<PacienteRowViewModel[]> {
  const response = await listPacientesApi({ size: 200, sort: "primeiroNome", ...params });
  return toPacienteRows(response);
}

export async function getPacienteById(id: number): Promise<PacienteApi> {
  return httpClient.get<PacienteApi>(`/pacientes/${id}`);
}

export async function getPacienteProfileById(id: number): Promise<PacienteProfileViewModel> {
  const response = await httpClient.get<PacienteProfileApi>(`/pacientes/${id}/perfil`);
  return toPacienteProfileViewModel(response);
}

export async function createPaciente(values: PacienteFormValues): Promise<PacienteApi> {
  return httpClient.post<PacienteApi, ReturnType<typeof toPacienteCreatePayload>>(
    "/pacientes",
    toPacienteCreatePayload(values),
  );
}

export async function updatePaciente(id: number, values: PacienteFormValues): Promise<PacienteApi> {
  return httpClient.put<PacienteApi, ReturnType<typeof toPacienteUpdatePayload>>(
    `/pacientes/${id}`,
    toPacienteUpdatePayload(values),
  );
}
