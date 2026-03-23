import { useCallback, useMemo } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import { emptyPageResponse } from "../shared/types";
import {
  createPaciente,
  getPacienteById,
  getPacienteProfileById,
  listPacientesRows,
  updatePaciente,
} from "./service";
import type {
  PacienteApi,
  PacienteFormValues,
  PacienteListParams,
  PacienteProfileViewModel,
  PacienteRowsPageViewModel,
} from "./types";

const EMPTY_PARAMS: PacienteListParams = {};

export function usePacientesQuery(params?: PacienteListParams) {
  const serializedParams = JSON.stringify(params ?? EMPTY_PARAMS);
  const stableParams = useMemo(() => JSON.parse(serializedParams) as PacienteListParams, [serializedParams]);

  const queryFn = useCallback(() => {
    return listPacientesRows(stableParams);
  }, [stableParams]);

  return useApiQuery<PacienteRowsPageViewModel>(
    ["pacientes", "list", stableParams],
    emptyPageResponse(),
    queryFn,
  );
}

export function usePacienteByIdQuery(id: number | null) {
  const queryFn = useCallback(() => {
    if (id === null) return Promise.resolve(null);
    return getPacienteById(id);
  }, [id]);

  return useApiQuery<PacienteApi | null>(
    ["pacientes", "detail", id],
    null,
    queryFn,
  );
}

export function usePacienteProfileQuery(id: number | null) {
  const queryFn = useCallback(() => {
    if (id === null) return Promise.resolve(null);
    return getPacienteProfileById(id);
  }, [id]);

  return useApiQuery<PacienteProfileViewModel | null>(
    ["pacientes", "profile", id],
    null,
    queryFn,
  );
}

export function useCreatePacienteMutation() {
  return useApiMutation<PacienteFormValues, PacienteApi>(createPaciente);
}

export function useUpdatePacienteMutation() {
  return useApiMutation<{ id: number; values: PacienteFormValues }, PacienteApi>(
    ({ id, values }) => updatePaciente(id, values),
  );
}