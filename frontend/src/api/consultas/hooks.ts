import { useCallback } from "react";
import { useApiQuery } from "../shared/hooks";
import { listConsultasByPacienteId, listConsultasRows } from "./service";
import type { ConsultaHistoryRowViewModel, ConsultaRowViewModel } from "./types";

export function useConsultasQuery() {
  const queryFn = useCallback(() => listConsultasRows(), []);
  return useApiQuery<ConsultaRowViewModel[]>(["consultas", "list"], [], queryFn);
}

export function useConsultasByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as ConsultaHistoryRowViewModel[]);
    return listConsultasByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<ConsultaHistoryRowViewModel[]>(["consultas", "paciente", pacienteId], [], queryFn);
}
