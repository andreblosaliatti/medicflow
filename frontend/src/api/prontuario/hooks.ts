import { useCallback } from "react";
import { useApiQuery } from "../shared/hooks";
import { getProntuarioByPacienteId } from "./service";
import type { ProntuarioPaciente } from "./types";

export function useProntuarioByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve(null);
    return getProntuarioByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<ProntuarioPaciente | null>(["prontuario", "paciente", pacienteId], null, queryFn);
}
