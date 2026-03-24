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
  listOperationalPendingItems,
  listTodayConsultas,
  listUpcomingAppointments,
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
  OperationalPendingItemViewModel,
} from "./types";

const EMPTY_PARAMS: ConsultaListParams = {};

function useStableParams<TParams>(params: TParams): TParams {
  const serializedParams = JSON.stringify(params);
  return useMemo(() => JSON.parse(serializedParams) as TParams, [serializedParams]);
}

function useMutationWithRefresh<TVariables>(
  mutationFn: (variables: TVariables) => Promise<ConsultaDetailsViewModel>,
) {
  return useApiMutation<TVariables, ConsultaDetailsViewModel>(mutationFn);
}

export function useConsultasQuery(params?: ConsultaListParams) {
  const stableParams = useStableParams(params ?? EMPTY_PARAMS);
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
  const stableParams = useStableParams(params);
  const queryFn = useCallback(() => listAgendaEvents(stableParams), [stableParams]);

  return useApiQuery<AppointmentEventViewModel[]>(["consultas", "agenda", stableParams], [], queryFn);
}

export function useTodayConsultasQuery(params: ConsultaListParams) {
  const stableParams = useStableParams(params);
  const queryFn = useCallback(() => listTodayConsultas(stableParams), [stableParams]);

  return useApiQuery<ConsultaTodayItemViewModel[]>(["consultas", "today", stableParams], [], queryFn);
}

export function useUpcomingAppointmentsQuery(params: ConsultaListParams) {
  const stableParams = useStableParams(params);
  const queryFn = useCallback(() => listUpcomingAppointments(stableParams), [stableParams]);

  return useApiQuery<ConsultaRowViewModel[]>(["consultas", "upcoming", stableParams], [], queryFn);
}

export function useOperationalPendingItemsQuery(params: ConsultaListParams) {
  const stableParams = useStableParams(params);
  const queryFn = useCallback(() => listOperationalPendingItems(stableParams), [stableParams]);

  return useApiQuery<OperationalPendingItemViewModel[]>(["consultas", "pending", stableParams], [], queryFn);
}

export function useCreateConsultaMutation() {
  return useMutationWithRefresh<ConsultaCreatePayload>((payload) => createConsulta(payload));
}

export function useUpdateConsultaMutation() {
  return useMutationWithRefresh<{ id: number; payload: ConsultaUpdatePayload }>(({ id, payload }) => updateConsulta(id, payload));
}

export function useConfirmConsultaMutation() {
  return useMutationWithRefresh<number>((id) => confirmConsulta(id));
}

export function useCancelConsultaMutation() {
  return useMutationWithRefresh<number>((id) => cancelConsulta(id));
}

export function useStartConsultaMutation() {
  return useMutationWithRefresh<number>((id) => startConsulta(id));
}

export function useFinishConsultaMutation() {
  return useMutationWithRefresh<number>((id) => finishConsulta(id));
}
