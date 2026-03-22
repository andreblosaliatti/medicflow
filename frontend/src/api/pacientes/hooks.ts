import { useCallback } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import { createPaciente, getPacienteById, getPacienteProfileById, listPacientesRows, updatePaciente } from "./service";
import type { PacienteApi, PacienteFormValues, PacienteProfileViewModel, PacienteRowViewModel } from "./types";

export function usePacientesQuery() {
  const queryFn = useCallback(() => listPacientesRows(), []);
  return useApiQuery<PacienteRowViewModel[]>(["pacientes", "list"], [], queryFn);
}

export function usePacienteByIdQuery(id: number | null) {
  const queryFn = useCallback(() => {
    if (id === null) return Promise.resolve(null);
    return getPacienteById(id);
  }, [id]);

  return useApiQuery<PacienteApi | null>(["pacientes", "detail", id], null, queryFn);
}

export function usePacienteProfileQuery(id: number | null) {
  const queryFn = useCallback(() => {
    if (id === null) return Promise.resolve(null);
    return getPacienteProfileById(id);
  }, [id]);

  return useApiQuery<PacienteProfileViewModel | null>(["pacientes", "profile", id], null, queryFn);
}

export function useCreatePacienteMutation() {
  return useApiMutation<PacienteFormValues, PacienteApi>(createPaciente);
}

export function useUpdatePacienteMutation() {
  return useApiMutation<{ id: number; values: PacienteFormValues }, PacienteApi>(({ id, values }) =>
    updatePaciente(id, values),
  );
}
