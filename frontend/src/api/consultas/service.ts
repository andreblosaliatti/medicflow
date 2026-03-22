import { httpClient } from "../http";
import { listPacientesApi } from "../pacientes/service";
import { toConsultaRowViewModel } from "./mappers";
import type { ConsultaApi, ConsultaRowViewModel } from "./types";

export async function listConsultasApi(): Promise<ConsultaApi[]> {
  return httpClient.get<ConsultaApi[]>("/consultas");
}

export async function listConsultasRows(): Promise<ConsultaRowViewModel[]> {
  const [consultas, pacientes] = await Promise.all([listConsultasApi(), listPacientesApi()]);

  return consultas
    .slice()
    .sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime())
    .map((consulta) => toConsultaRowViewModel(consulta, pacientes));
}
