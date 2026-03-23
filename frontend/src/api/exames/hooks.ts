import { useCallback, useMemo } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import {
  createExameSolicitado,
  deleteExameSolicitado,
  listExamesByConsultaId,
  listExamesByPacienteId,
  searchExamesBase,
  updateExameById,
} from "./service";
import type {
  ExameBaseOptionViewModel,
  ExameSolicitadoPayload,
  ExameViewModel,
  UpdateExameRequest,
} from "./types";

export function useExamesByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as ExameViewModel[]);
    return listExamesByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<ExameViewModel[]>(["exames", "paciente", pacienteId], [], queryFn);
}

export function useUpdateExameMutation() {
  return useApiMutation<UpdateExameRequest, ExameViewModel>(updateExameById);
}

export function useExamesByConsultaQuery(consultaId: number | null) {
  const queryFn = useCallback(() => {
    if (consultaId === null) return Promise.resolve([] as ExameViewModel[]);
    return listExamesByConsultaId(consultaId);
  }, [consultaId]);

  return useApiQuery<ExameViewModel[]>(["exames", "consulta", consultaId], [], queryFn);
}

export function useExameBaseSearchQuery(query: string) {
  const normalizedQuery = query.trim();
  const stableQuery = useMemo(() => normalizedQuery, [normalizedQuery]);
  const queryFn = useCallback(() => searchExamesBase(stableQuery), [stableQuery]);

  return useApiQuery<ExameBaseOptionViewModel[]>(["exames-base", stableQuery], [], queryFn);
}

export function useCreateExameSolicitadoMutation() {
  return useApiMutation<ExameSolicitadoPayload, ExameViewModel>(createExameSolicitado);
}

export function useDeleteExameSolicitadoMutation() {
  return useApiMutation<number, void>(deleteExameSolicitado);
}
