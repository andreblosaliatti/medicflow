import { useCallback, useEffect, useMemo, useState } from "react";
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
import { notifyConsultasChanged, subscribeConsultasChanged } from "./events";
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

function useConsultasVersion() {
  const [version, setVersion] = useState(0);

  useEffect(() => subscribeConsultasChanged(() => setVersion((current) => current + 1)), []);

  return version;
}

async function runAndNotify<TResult>(action: () => Promise<TResult>) {
  const result = await action();
  notifyConsultasChanged();
  return result;
}

export function useConsultasQuery(params?: ConsultaListParams) {
  const version = useConsultasVersion();
  const serializedParams = JSON.stringify(params ?? EMPTY_PARAMS);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as ConsultaListParams, [serializedParams]);
  const queryFn = useCallback(() => listConsultasRows(stableParams), [stableParams]);

  return useApiQuery<ConsultaRowViewModel[]>(["consultas", "list", stableParams, version], [], queryFn);
}

export function useConsultasByPacienteQuery(pacienteId: number | null) {
  const version = useConsultasVersion();
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as ConsultaHistoryRowViewModel[]);
    return listConsultasByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<ConsultaHistoryRowViewModel[]>(["consultas", "paciente", pacienteId, version], [], queryFn);
}

export function useConsultaDetailsQuery(id: number | null) {
  const version = useConsultasVersion();
  const queryFn = useCallback(() => {
    if (id === null) return Promise.resolve(null);
    return getConsultaDetails(id);
  }, [id]);

  return useApiQuery<ConsultaDetailsViewModel | null>(["consultas", "detail", id, version], null, queryFn);
}

export function useConsultaMetadataQuery() {
  const queryFn = useCallback(() => fetchConsultaMetadata(), []);
  return useApiQuery<ConsultaMetadataApi | null>(["consultas", "metadata"], null, queryFn);
}

export function useAgendaEventsQuery(params: ConsultaListParams) {
  const version = useConsultasVersion();
  const serializedParams = JSON.stringify(params);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as ConsultaListParams, [serializedParams]);
  const queryFn = useCallback(() => listAgendaEvents(stableParams), [stableParams]);

  return useApiQuery<AppointmentEventViewModel[]>(["consultas", "agenda", stableParams, version], [], queryFn);
}

export function useTodayConsultasQuery(params: ConsultaListParams) {
  const version = useConsultasVersion();
  const serializedParams = JSON.stringify(params);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as ConsultaListParams, [serializedParams]);
  const queryFn = useCallback(() => listTodayConsultas(stableParams), [stableParams]);

  return useApiQuery<ConsultaTodayItemViewModel[]>(["consultas", "today", stableParams, version], [], queryFn);
}

export function useCreateConsultaMutation() {
  return useApiMutation<ConsultaCreatePayload, ConsultaDetailsViewModel>((payload) => runAndNotify(() => createConsulta(payload)));
}

export function useUpdateConsultaMutation() {
  return useApiMutation<{ id: number; payload: ConsultaUpdatePayload }, ConsultaDetailsViewModel>(
    ({ id, payload }) => runAndNotify(() => updateConsulta(id, payload)),
  );
}

export function useConfirmConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>((id) => runAndNotify(() => confirmConsulta(id)));
}

export function useCancelConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>((id) => runAndNotify(() => cancelConsulta(id)));
}

export function useStartConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>((id) => runAndNotify(() => startConsulta(id)));
}

export function useFinishConsultaMutation() {
  return useApiMutation<number, ConsultaDetailsViewModel>((id) => runAndNotify(() => finishConsulta(id)));
}
