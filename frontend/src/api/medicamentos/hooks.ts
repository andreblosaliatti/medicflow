import { useCallback } from "react";
import { useApiMutation, useApiQuery } from "../shared/hooks";
import { duplicateMedicamentoById, listMedicamentosByPacienteId } from "./service";
import type { MedicamentoViewModel } from "./types";

export function useMedicamentosByPacienteQuery(pacienteId: number | null) {
  const queryFn = useCallback(() => {
    if (pacienteId === null) return Promise.resolve([] as MedicamentoViewModel[]);
    return listMedicamentosByPacienteId(pacienteId);
  }, [pacienteId]);

  return useApiQuery<MedicamentoViewModel[]>(["medicamentos", pacienteId], [], queryFn);
}

export function useDuplicateMedicamentoMutation() {
  return useApiMutation<number, MedicamentoViewModel>(duplicateMedicamentoById);
}
