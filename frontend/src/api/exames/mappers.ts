import type { ExameApi, ExameBaseApi, ExameBaseOptionViewModel, ExameViewModel } from "./types";

export function toExameViewModel(exame: ExameApi): ExameViewModel {
  return {
    id: exame.id,
    status: exame.status,
    justificativa: exame.justificativa,
    observacoes: exame.observacoes,
    dataColeta: exame.dataColeta,
    dataResultado: exame.dataResultado,
    consultaId: exame.consultaId,
    pacienteId: exame.pacienteId,
    exameBaseId: exame.exameBaseId ?? null,
    nome: exame.exameNome ?? exame.nome ?? "Exame",
  };
}

export function toExameBaseOption(exame: ExameBaseApi): ExameBaseOptionViewModel {
  return {
    id: exame.id,
    label: exame.nome,
    subtitle: [exame.codigoTuss, exame.tipo].filter(Boolean).join(" • "),
  };
}
