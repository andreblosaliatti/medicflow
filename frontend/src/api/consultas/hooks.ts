import { useCallback, useMemo } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import {
  cancelConsulta,
  confirmConsulta,
  createConsulta,
  fetchConsultaMetadata,
  finishConsulta,
  getConsultaDetails,
  listAgendaEvents,
  listConsultasByPacienteId,
  listConsultasRows,
  listTodayConsultas,
  startConsulta,
  updateConsulta,
} from "./service";
import type {
  AppointmentEventViewModel,
  ConsultaCreatePayload,
  ConsultaDetailsViewModel,
  ConsultaHistoryRowViewModel,
  ConsultaListParams,
  ConsultaMetadataApi,
  ConsultaRowViewModel,
  ConsultaTodayItemViewModel,
  ConsultaUpdatePayload,
} from "./types";

const EMPTY_PARAMS: ConsultaListParams = {};

export function useConsultasQuery(params?: ConsultaListParams) {
  const serializedParams = JSON.stringify(params ?? EMPTY_PARAMS);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as ConsultaListParams, [serializedParams]);
  const queryFn = useCallback(() => listConsultasRows(stableParams), [stableParams]);

  return useApiQuery<ConsultaRowViewModel[]>(["consultas", "list", stableParams], [], queryFn);
}

export function useConsultasByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as ConsultaHistoryRowViewModel[]);
    return listConsultasByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<ConsultaHistoryRowViewModel[]>(["consultas", "paciente", pacienteId], [], queryFn);
}

export function useConsultaDetailsQuery(id: number | null) {
  const queryFn = useCallback(() => {
    if (id === null) return Promise.resolve(null);
    return getConsultaDetails(id);
  }, [id]);

  return useApiQuery<ConsultaDetailsViewModel | null>(["consultas", "detail", id], null, queryFn);
}

export function useConsultaMetadataQuery() {
  const queryFn = useCallback(() => fetchConsultaMetadata(), []);
  return useApiQuery<ConsultaMetadataApi | null>(["consultas", "metadata"], null, queryFn);
}

export function useAgendaEventsQuery(params: ConsultaListParams) {
  const serializedParams = JSON.stringify(params);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as ConsultaListParams, [serializedParams]);
  const queryFn = useCallback(() => listAgendaEvents(stableParams), [stableParams]);

  return useApiQuery<AppointmentEventViewModel[]>(["consultas", "agenda", stableParams], [], queryFn);
}

export function useTodayConsultasQuery(params: ConsultaListParams) {
  const serializedParams = JSON.stringify(params);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as ConsultaListParams, [serializedParams]);
  const queryFn = useCallback(() => listTodayConsultas(stableParams), [stableParams]);

  return useApiQuery<ConsultaTodayItemViewModel[]>(["consultas", "today", stableParams], [], queryFn);
}

export function useCreateConsultaMutation() {
  return useApiMutation<ConsultaCreatePayload, ConsultaDetailsViewModel>(createConsulta);
}

export function useUpdateConsultaMutation() {
  return useApiMutation<{ id: number; payload: ConsultaUpdatePayload }, ConsultaDetailsViewModel>(
    ({ id, payload }) => updateConsulta(id, payload),
  );
}

export function useConfirmConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>(confirmConsulta);
}

export function useCancelConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>(cancelConsulta);
}

export function useStartConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>(startConsulta);
}

export function useFinishConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>(finishConsulta);
}
