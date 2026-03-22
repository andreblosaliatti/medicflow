import type { StatusConsulta } from "../../domain/enums/statusConsulta";
import { statusLabel } from "../../domain/ui/statusLabel";
import { statusTone } from "../../domain/ui/statusTone";
import type { ConsultaApi, ConsultaHistoryRowViewModel, ConsultaRowViewModel, ConsultaTableItemApi } from "./types";

function two(value: number) {
  return String(value).padStart(2, "0");
}

function parseDate(value: string | null | undefined) {
  if (!value) return null;
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function formatDateTime(iso: string | null | undefined) {
  const date = parseDate(iso);
  if (!date) return "—";
  return `${two(date.getDate())}/${two(date.getMonth() + 1)}/${date.getFullYear()} ${two(date.getHours())}:${two(date.getMinutes())}`;
}

function formatDate(iso: string | null | undefined) {
  const date = parseDate(iso);
  if (!date) return "—";
  return `${two(date.getDate())}/${two(date.getMonth() + 1)}/${date.getFullYear()}`;
}

function formatTime(iso: string | null | undefined) {
  const date = parseDate(iso);
  if (!date) return "—";
  return `${two(date.getHours())}:${two(date.getMinutes())}`;
}

function normalizeHistoryStatus(status: string | null | undefined): ConsultaHistoryRowViewModel["status"] {
  if (status === "CONFIRMADA" || status === "EM_ATENDIMENTO" || status === "CONCLUIDA" || status === "CANCELADA") {
    return status;
  }

  return "AGENDADA";
}

function normalizeStatus(status: string | null | undefined): StatusConsulta {
  if (status === "CONFIRMADA" || status === "EM_ATENDIMENTO" || status === "CONCLUIDA" || status === "CANCELADA") {
    return status;
  }

  return "AGENDADA";
}

export function toConsultaRowViewModel(consulta: ConsultaApi): ConsultaRowViewModel {
  const normalizedStatus = normalizeStatus(consulta.status);

  return {
    id: String(consulta.id),
    dataHoraLabel: formatDateTime(consulta.dataHora),
    pacienteId: consulta.pacienteId ?? 0,
    pacienteNome: consulta.pacienteNome?.trim() || "Paciente",
    medicoNome: consulta.medicoNome?.trim() || "Profissional não informado",
    tipo: consulta.tipo,
    duracaoMinutos: consulta.duracaoMinutos ?? 0,
    status: normalizedStatus,
    statusLabel: statusLabel(normalizedStatus),
    statusTone: statusTone(normalizedStatus),
  };
}

export function toConsultaHistoryRowViewModel(consulta: ConsultaTableItemApi): ConsultaHistoryRowViewModel {
  return {
    id: String(consulta.id),
    data: formatDate(consulta.dataHora),
    hora: formatTime(consulta.dataHora),
    profissional: consulta.medicoNome?.trim() || "Profissional não informado",
    status: normalizeHistoryStatus(consulta.status),
  };
}
