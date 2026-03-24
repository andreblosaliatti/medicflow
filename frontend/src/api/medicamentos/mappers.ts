import type {
  MedicamentoApi,
  MedicamentoBaseApi,
  MedicamentoBaseOptionViewModel,
  MedicamentoViewModel,
} from "./types";

export function toMedicamentoViewModel(medicamento: MedicamentoApi): MedicamentoViewModel {
  return {
    id: medicamento.id,
    nome: medicamento.nome,
    dosagem: medicamento.dosagem,
    frequencia: medicamento.frequencia,
    via: medicamento.via,
    consultaId: medicamento.consultaId,
    medicamentoBaseId: medicamento.medicamentoBaseId ?? null,
  };
}

export function toMedicamentoBaseOption(medicamento: MedicamentoBaseApi): MedicamentoBaseOptionViewModel {
  const title = medicamento.nomeComercial || medicamento.principioAtivo || medicamento.dcb;
  const subtitle = [medicamento.nomeComercial, medicamento.principioAtivo].filter(Boolean).join(" • ");

  return {
    id: medicamento.id,
    label: title,
    subtitle,
    dosagemPadrao: medicamento.dosagemPadrao,
    viaAdministracao: medicamento.viaAdministracao,
  };
}
