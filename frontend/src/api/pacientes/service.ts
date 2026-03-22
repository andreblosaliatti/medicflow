import { httpClient } from "../http";
import { listConsultasApi } from "../consultas/service";
import { toPacienteRowViewModel } from "./mappers";
import type { PacienteApi, PacienteRowViewModel } from "./types";

export async function listPacientesApi(): Promise<PacienteApi[]> {
  return httpClient.get<PacienteApi[]>("/pacientes");
}

export async function listPacientesRows(): Promise<PacienteRowViewModel[]> {
  const [pacientes, consultas] = await Promise.all([listPacientesApi(), listConsultasApi()]);
  return pacientes.map((paciente) => toPacienteRowViewModel(paciente, consultas));
}
