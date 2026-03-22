import { useCallback } from "react";
import { useApiQuery } from "../shared/hooks";
import { listConsultasRows } from "./service";
import type { ConsultaRowViewModel } from "./types";

export function useConsultasQuery() {
  const queryFn = useCallback(() => listConsultasRows(), []);
  return useApiQuery<ConsultaRowViewModel[]>(["consultas", "list"], [], queryFn);
}
