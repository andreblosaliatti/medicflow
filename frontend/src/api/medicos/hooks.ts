import { useCallback } from "react";

import { useApiQuery } from "../shared/hooks";
import { listMedicoOptions } from "./service";
import type { MedicoOptionViewModel } from "./types";

export function useMedicoOptionsQuery(enabled = true) {
  const queryFn = useCallback(() => {
    if (!enabled) return Promise.resolve([] as MedicoOptionViewModel[]);
    return listMedicoOptions();
  }, [enabled]);

  return useApiQuery<MedicoOptionViewModel[]>(
    ["medicos", "options", enabled],
    [],
    queryFn,
  );
}
