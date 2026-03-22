import { useCallback } from "react";
import { useApiQuery } from "../shared/hooks";
import { listPacientesRows } from "./service";
import type { PacienteRowViewModel } from "./types";

export function usePacientesQuery() {
  const queryFn = useCallback(() => listPacientesRows(), []);
  return useApiQuery<PacienteRowViewModel[]>(["pacientes", "list"], [], queryFn);
}
