import { api, unwrapResponse } from "../../lib/api";
import type { ProntuarioPaciente } from "./types";

export async function getProntuarioByPacienteId(pacienteId: number): Promise<ProntuarioPaciente> {
  return unwrapResponse(api.get<ProntuarioPaciente>(`/pacientes/${pacienteId}/prontuario`));
}
