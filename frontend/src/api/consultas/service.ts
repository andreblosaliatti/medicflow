import { api, unwrapResponse } from "../../lib/api";
import type { StatusConsulta } from "../../domain/enums/statusConsulta";
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
  OperationalPendingItemViewModel,
} from "./types";

function buildConsultaParams(params: ConsultaListParams = {}): ConsultaListParams {
  return {
    page: 0,
    size: 200,
    sort: "dataHora,desc",
    ...params,
  };
}

function parseLabelDateTime(value: string) {
  const matches = value.match(/^(\d{2})\/(\d{2})\/(\d{4})\s+(\d{2}):(\d{2})$/);
  if (!matches) return null;

  const [, dd, mm, yyyy, hh, min] = matches;
  const date = new Date(Number(yyyy), Number(mm) - 1, Number(dd), Number(hh), Number(min), 0, 0);
  return Number.isNaN(date.getTime()) ? null : date;
}

function buildOperationalPending(item: ConsultaRowViewModel, now: Date): OperationalPendingItemViewModel {
  const dataHora = parseLabelDateTime(item.dataHoraLabel);
  const diffMs = dataHora ? dataHora.getTime() - now.getTime() : 0;

  const isOverdue = dataHora ? diffMs < 0 : false;
  const within24h = dataHora ? diffMs >= 0 && diffMs <= 24 * 60 * 60 * 1000 : false;
  const within72h = dataHora ? diffMs > 24 * 60 * 60 * 1000 && diffMs <= 72 * 60 * 60 * 1000 : false;

  let prioridade: OperationalPendingItemViewModel["prioridade"] = "BAIXA";
  if (isOverdue || (within24h && item.status === "AGENDADA")) prioridade = "ALTA";
  else if (within72h) prioridade = "MEDIA";

  const pendenciaLabel = item.status === "AGENDADA"
    ? "Confirmar consulta"
    : item.status === "CONFIRMADA"
      ? "Iniciar atendimento"
      : item.status === "EM_ATENDIMENTO"
        ? "Finalizar atendimento"
        : "Acompanhar";

  return {
    ...item,
    prioridade,
    prioridadeTone: prioridade === "ALTA" ? "danger" : prioridade === "MEDIA" ? "warn" : "muted",
    pendenciaLabel,
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

export async function listUpcomingAppointments(params: ConsultaListParams = {}): Promise<ConsultaRowViewModel[]> {
  const response = await listConsultasRows(params);
  const now = Date.now();

  return response
    .filter((consulta) => {
      const parsed = parseLabelDateTime(consulta.dataHoraLabel);
      if (!parsed) return false;
      if (consulta.status === "CANCELADA" || consulta.status === "CONCLUIDA") return false;
      return parsed.getTime() >= now;
    })
    .sort((a, b) => {
      const da = parseLabelDateTime(a.dataHoraLabel)?.getTime() ?? 0;
      const db = parseLabelDateTime(b.dataHoraLabel)?.getTime() ?? 0;
      return da - db;
    });
}

export async function listOperationalPendingItems(params: ConsultaListParams = {}): Promise<OperationalPendingItemViewModel[]> {
  const rows = await listConsultasRows(params);
  const now = new Date();
  const actionableStatuses = new Set<StatusConsulta>(["AGENDADA", "CONFIRMADA", "EM_ATENDIMENTO"]);

  return rows
    .filter((row) => actionableStatuses.has(row.status))
    .map((row) => buildOperationalPending(row, now))
    .sort((a, b) => {
      const toneScore = { danger: 0, warn: 1, muted: 2 };
      const toneDiff = toneScore[a.prioridadeTone] - toneScore[b.prioridadeTone];
      if (toneDiff !== 0) return toneDiff;
      const da = parseLabelDateTime(a.dataHoraLabel)?.getTime() ?? Number.MAX_SAFE_INTEGER;
      const db = parseLabelDateTime(b.dataHoraLabel)?.getTime() ?? Number.MAX_SAFE_INTEGER;
      return da - db;
    });
}
