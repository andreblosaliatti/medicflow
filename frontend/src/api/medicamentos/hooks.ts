import { useCallback, useMemo } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import {
  createMedicamentoPrescrito,
  deleteMedicamentoPrescrito,
  listMedicamentosByConsultaId,
  listMedicamentosByPacienteId,
  searchMedicamentosBase,
  updateMedicamentoPrescrito,
} from "./service";
import type {
  MedicamentoBaseOptionViewModel,
  MedicamentoPrescritoPayload,
  MedicamentoViewModel,
} from "./types";

export function useMedicamentosByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as MedicamentoViewModel[]);
    return listMedicamentosByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<MedicamentoViewModel[]>(["medicamentos", "paciente", pacienteId], [], queryFn);
}


export function useMedicamentosByConsultaQuery(consultaId: number | null) {
  const queryFn = useCallback(() => {
    if (consultaId === null) return Promise.resolve([] as MedicamentoViewModel[]);
    return listMedicamentosByConsultaId(consultaId);
  }, [consultaId]);

  return useApiQuery<MedicamentoViewModel[]>(["medicamentos", "consulta", consultaId], [], queryFn);
}

export function useMedicamentoBaseSearchQuery(query: string) {
  const normalizedQuery = query.trim();
  const stableQuery = useMemo(() => normalizedQuery, [normalizedQuery]);
  const queryFn = useCallback(() => searchMedicamentosBase(stableQuery), [stableQuery]);

  return useApiQuery<MedicamentoBaseOptionViewModel[]>(["medicamentos-base", stableQuery], [], queryFn);
}

export function useCreateMedicamentoPrescritoMutation() {
  return useApiMutation<{ consultaId: number; payload: MedicamentoPrescritoPayload }, MedicamentoViewModel>(
    ({ consultaId, payload }) => createMedicamentoPrescrito(consultaId, payload),
  );
}

export function useUpdateMedicamentoPrescritoMutation() {
  return useApiMutation<{ medicamentoId: number; payload: MedicamentoPrescritoPayload }, MedicamentoViewModel>(
    ({ medicamentoId, payload }) => updateMedicamentoPrescrito(medicamentoId, payload),
  );
}

export function useDeleteMedicamentoPrescritoMutation() {
  return useApiMutation<number, void>(deleteMedicamentoPrescrito);
}

export function useMedicamentosByConsultaQuery(consultaId: number | null) {
  const queryFn = useCallback(() => {
    if (consultaId === null) return Promise.resolve([] as MedicamentoViewModel[]);
    return listMedicamentosByConsultaId(consultaId);
  }, [consultaId]);

  return useApiQuery<MedicamentoViewModel[]>(["medicamentos", "consulta", consultaId], [], queryFn);
}

export function useMedicamentoBaseSearchQuery(query: string) {
  const normalizedQuery = query.trim();
  const stableQuery = useMemo(() => normalizedQuery, [normalizedQuery]);
  const queryFn = useCallback(() => searchMedicamentosBase(stableQuery), [stableQuery]);

  return useApiQuery<MedicamentoBaseOptionViewModel[]>(["medicamentos-base", stableQuery], [], queryFn);
}

export function useCreateMedicamentoPrescritoMutation() {
  return useApiMutation<{ consultaId: number; payload: MedicamentoPrescritoPayload }, MedicamentoViewModel>(
    ({ consultaId, payload }) => createMedicamentoPrescrito(consultaId, payload),
  );
}

export function useUpdateMedicamentoPrescritoMutation() {
  return useApiMutation<{ medicamentoId: number; payload: MedicamentoPrescritoPayload }, MedicamentoViewModel>(
    ({ medicamentoId, payload }) => updateMedicamentoPrescrito(medicamentoId, payload),
  );
}

export function useDeleteMedicamentoPrescritoMutation() {
  return useApiMutation<number, void>(deleteMedicamentoPrescrito);
}
