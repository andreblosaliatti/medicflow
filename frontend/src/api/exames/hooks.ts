import { useCallback } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import { listExamesByPacienteId, updateExameById } from "./service";
import type { ExameViewModel, UpdateExameRequest } from "./types";

export function useExamesByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as ExameViewModel[]);
    return listExamesByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<ExameViewModel[]>(["exames", pacienteId], [], queryFn);
}

export function useUpdateExameMutation() {
  return useApiMutation<UpdateExameRequest, ExameViewModel>(updateExameById);
}
