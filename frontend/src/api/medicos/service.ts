import { api, unwrapResponse } from "../../lib/api";
import type { MedicoOptionViewModel, MedicoSelectApi } from "./types";

function toMedicoOption(medico: MedicoSelectApi): MedicoOptionViewModel {
  const nome = medico.nomeCompleto?.trim() || `Medico #${medico.id}`;
  const details = [medico.crm, medico.especialidade]
    .map((item) => item?.trim())
    .filter(Boolean);

  return {
    id: medico.id,
    label: details.length > 0 ? `${nome} - ${details.join(" - ")}` : nome,
  };
}

export async function listMedicoOptions(): Promise<MedicoOptionViewModel[]> {
  const response = await unwrapResponse(
    api.get<MedicoSelectApi[]>("/medicos/resumo", { params: { limite: 50 } }),
  );

  return response.map(toMedicoOption);
}
