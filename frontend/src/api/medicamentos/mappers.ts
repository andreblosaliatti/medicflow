import type { MedicamentoApi, MedicamentoViewModel } from "./types";

export function toMedicamentoViewModel(medicamento: MedicamentoApi): MedicamentoViewModel {
  return {
    id: medicamento.id,
    nome: medicamento.nome,
    dosagem: medicamento.dosagem,
    frequencia: medicamento.frequencia,
    via: medicamento.via,
    consultaId: medicamento.consultaId,
  };
}
