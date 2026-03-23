import { api, unwrapResponse } from "../../lib/api";
import type { PageResponse } from "../shared/types";
import {
  toAppointmentEventViewModel,
  toConsultaDetailsViewModel,
  toConsultaHistoryRowViewModel,
  toConsultaRowViewModel,
  toConsultaTodayItemViewModel,
} from "./mappers";
import type {
  AppointmentEventViewModel,
  ConsultaAgendaItemApi,
  ConsultaCreatePayload,
  ConsultaDetailsApi,
  ConsultaDetailsViewModel,
  ConsultaHistoryRowViewModel,
  ConsultaListParams,
  ConsultaMetadataApi,
  ConsultaRowViewModel,
  ConsultaTableItemApi,
  ConsultaTodayItemViewModel,
  ConsultaUpdatePayload,
} from "./types";

function buildConsultaParams(params: ConsultaListParams = {}): ConsultaListParams {
  return {
    page: 0,
    size: 200,
    sort: "dataHora,desc",
    ...params,
  };
}

export async function listConsultasApi(params: ConsultaListParams = {}): Promise<PageResponse<ConsultaTableItemApi>> {
  return unwrapResponse(api.get<PageResponse<ConsultaTableItemApi>>("/consultas/tabela", { params: buildConsultaParams(params) }));
}

export async function listConsultasRows(params: ConsultaListParams = {}): Promise<ConsultaRowViewModel[]> {
  const response = await listConsultasApi(params);
  return response.content
    .slice()
    .sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime())
    .map(toConsultaRowViewModel);
}

export async function listConsultasByPacienteId(pacienteId: number): Promise<ConsultaHistoryRowViewModel[]> {
  const response = await listConsultasApi({ pacienteId, size: 50, sort: "dataHora,desc" });
  return response.content.map(toConsultaHistoryRowViewModel);
}

export async function getConsultaDetails(id: number): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.get<ConsultaDetailsApi>(`/consultas/${id}`));
  return toConsultaDetailsViewModel(response);
}

export async function fetchConsultaMetadata(): Promise<ConsultaMetadataApi> {
  return unwrapResponse(api.get<ConsultaMetadataApi>("/consultas/metadata"));
}

export async function createConsulta(payload: ConsultaCreatePayload): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.post<ConsultaDetailsApi, ConsultaCreatePayload>("/consultas", payload));
  return toConsultaDetailsViewModel(response);
}

export async function updateConsulta(id: number, payload: ConsultaUpdatePayload): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.put<ConsultaDetailsApi, ConsultaUpdatePayload>(`/consultas/${id}`, payload));
  return toConsultaDetailsViewModel(response);
}

export async function confirmConsulta(id: number): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.patch<ConsultaDetailsApi>(`/consultas/${id}/confirmar`));
  return toConsultaDetailsViewModel(response);
}

export async function cancelConsulta(id: number): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.patch<ConsultaDetailsApi>(`/consultas/${id}/cancelar`));
  return toConsultaDetailsViewModel(response);
}

export async function startConsulta(id: number): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.patch<ConsultaDetailsApi>(`/consultas/${id}/iniciar-atendimento`));
  return toConsultaDetailsViewModel(response);
}

export async function finishConsulta(id: number): Promise<ConsultaDetailsViewModel> {
  const response = await unwrapResponse(api.patch<ConsultaDetailsApi>(`/consultas/${id}/finalizar-atendimento`));
  return toConsultaDetailsViewModel(response);
}

export async function listAgendaEvents(params: ConsultaListParams = {}): Promise<AppointmentEventViewModel[]> {
  const response = await unwrapResponse(api.get<PageResponse<ConsultaAgendaItemApi>>("/consultas/agenda", {
    params: buildConsultaParams(params),
  }));

  return response.content.map(toAppointmentEventViewModel);
}

export async function listTodayConsultas(params: ConsultaListParams = {}): Promise<ConsultaTodayItemViewModel[]> {
  const response = await listConsultasApi(params);
  return response.content
    .slice()
    .sort((a, b) => new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime())
    .map(toConsultaTodayItemViewModel);
}
