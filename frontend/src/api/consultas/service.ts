import { api, unwrapResponse } from "../../lib/api";
import type { PageResponse } from "../shared/types";
import { toConsultaHistoryRowViewModel, toConsultaRowViewModel } from "./mappers";
import type { ConsultaApi, ConsultaHistoryRowViewModel, ConsultaRowViewModel, ConsultaTableItemApi } from "./types";

export async function listConsultasApi(): Promise<ConsultaApi[]> {
  const response = await unwrapResponse(api.get<PageResponse<ConsultaApi>>("/consultas/tabela", { params: { size: 200 } }));
  return response.content;
}

export async function listConsultasRows(): Promise<ConsultaRowViewModel[]> {
  const consultas = await listConsultasApi();

  return consultas
    .slice()
    .sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime())
    .map(toConsultaRowViewModel);
}

export async function listConsultasByPacienteId(pacienteId: number): Promise<ConsultaHistoryRowViewModel[]> {
  const response = await unwrapResponse(api.get<PageResponse<ConsultaTableItemApi>>("/consultas/tabela", {
    params: {
      pacienteId,
      size: 50,
    },
  }));

  return response.content.map(toConsultaHistoryRowViewModel);
}
