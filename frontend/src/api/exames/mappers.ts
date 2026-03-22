import type { ExameApi, ExameViewModel } from "./types";

export function toExameViewModel(exame: ExameApi): ExameViewModel {
  return { ...exame };
}
