import type { MeioPagamento, StatusConsulta, TipoConsulta } from "../../domain/enums/statusConsulta";
import { statusLabel } from "../../domain/ui/statusLabel";
import { statusTone } from "../../domain/ui/statusTone";
import type {
  AppointmentEventViewModel,
  ConsultaAgendaItemApi,
  ConsultaApi,
  ConsultaDetailsApi,
  ConsultaDetailsViewModel,
  ConsultaHistoryRowViewModel,
  ConsultaRowViewModel,
  ConsultaTodayItemViewModel,
} from "./types";

function two(value: number) {
  return String(value).padStart(2, "0");
}

function parseDate(value: string | null | undefined) {
  if (!value) return null;
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function normalizeTipo(tipo: string | null | undefined): TipoConsulta {
  if (tipo === "TELECONSULTA" || tipo === "RETORNO") return tipo;
  return "PRESENCIAL";
}

function normalizeMeioPagamento(meioPagamento: string | null | undefined): MeioPagamento | null {
  if (
    meioPagamento === "DEBITO"
    || meioPagamento === "CREDITO"
    || meioPagamento === "PIX"
    || meioPagamento === "DINHEIRO"
  ) {
    return meioPagamento;
  }

  if (meioPagamento === "CARTAO") {
    return "CREDITO";
  }

  return null;
}

function normalizeStatus(status: string | null | undefined): StatusConsulta {
  if (
    status === "CONFIRMADA"
    || status === "EM_ATENDIMENTO"
    || status === "CONCLUIDA"
    || status === "CANCELADA"
    || status === "NAO_COMPARECEU"
  ) {
    return status;
  }

  return "AGENDADA";
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

function formatMoneyBR(value: number | null | undefined) {
  const amount = Number(value ?? 0);
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(Number.isFinite(amount) ? amount : 0);
}

function formatMeioPagamento(value: MeioPagamento | null) {
  if (value === "DEBITO") return "Cartão de débito";
  if (value === "CREDITO") return "Cartão de crédito";
  if (value === "DINHEIRO") return "Dinheiro";
  if (value === "PIX") return "PIX";
  return "—";
}

export function toConsultaRowViewModel(consulta: ConsultaApi): ConsultaRowViewModel {
  const normalizedStatus = normalizeStatus(consulta.status);

  return {
    id: String(consulta.id),
    dataHoraLabel: formatDateTime(consulta.dataHora),
    pacienteId: consulta.pacienteId ?? 0,
    pacienteNome: consulta.pacienteNome?.trim() || "Paciente",
    medicoId: consulta.medicoId,
    medicoNome: consulta.medicoNome?.trim() || "Profissional não informado",
    tipo: normalizeTipo(consulta.tipo),
    duracaoMinutos: consulta.duracaoMinutos ?? 0,
    status: normalizedStatus,
    statusLabel: statusLabel(normalizedStatus),
    statusTone: statusTone(normalizedStatus),
  };
}

export function toConsultaHistoryRowViewModel(consulta: ConsultaApi): ConsultaHistoryRowViewModel {
  return {
    id: String(consulta.id),
    data: formatDate(consulta.dataHora),
    hora: formatTime(consulta.dataHora),
    profissional: consulta.medicoNome?.trim() || "Profissional não informado",
    status: normalizeStatus(consulta.status),
  };
}

export function toConsultaDetailsViewModel(consulta: ConsultaDetailsApi): ConsultaDetailsViewModel {
  const normalizedStatus = normalizeStatus(consulta.status);
  const meioPagamento = normalizeMeioPagamento(consulta.meioPagamento);
  const tipo = normalizeTipo(consulta.tipo);
  const valorConsulta = Number(consulta.valorConsulta ?? 0);
  const pago = Boolean(consulta.pago);

  return {
    id: String(consulta.id),
    dataHora: consulta.dataHora,
    dataHoraLabel: formatDateTime(consulta.dataHora),
    pacienteId: consulta.pacienteId ?? 0,
    pacienteNome: consulta.pacienteNome?.trim() || "Paciente",
    medicoId: consulta.medicoId,
    medicoNome: consulta.medicoNome?.trim() || "Profissional não informado",
    tipo,
    duracaoMinutos: consulta.duracaoMinutos ?? 0,
    status: normalizedStatus,
    statusLabel: statusLabel(normalizedStatus),
    statusTone: statusTone(normalizedStatus),
    motivo: consulta.motivo?.trim() || "—",
    sala: tipo === "TELECONSULTA" ? "Teleconsulta" : "Sala 01",
    telefoneContato: null,
    valorConsulta,
    valorConsultaLabel: formatMoneyBR(valorConsulta),
    pago,
    pagoLabel: pago ? "Sim" : "Não",
    meioPagamento,
    meioPagamentoLabel: formatMeioPagamento(meioPagamento),
    dataPagamento: consulta.dataPagamento,
    teleconsulta: Boolean(consulta.teleconsulta),
    linkAcesso: consulta.linkAcesso,
    retorno: Boolean(consulta.retorno),
    dataLimiteRetorno: consulta.dataLimiteRetorno,
    planoSaude: consulta.planoSaude,
    numeroCarteirinha: consulta.numeroCarteirinha,
    anamnese: consulta.anamnese,
    exameFisico: consulta.exameFisico,
    diagnostico: consulta.diagnostico,
    prescricao: consulta.prescricao,
    observacoes: consulta.observacoes,
  };
}

export function toAppointmentEventViewModel(consulta: ConsultaAgendaItemApi): AppointmentEventViewModel {
  const start = parseDate(consulta.dataHora) ?? new Date();
  const duration = consulta.duracaoMinutos ?? 30;
  const end = new Date(start.getTime() + duration * 60000);
  const tipo = normalizeTipo(consulta.tipo);

  return {
    id: String(consulta.id),
    patientId: consulta.pacienteId,
    patientName: consulta.pacienteNome?.trim() || "Paciente",
    professionalId: consulta.medicoId,
    professionalName: consulta.medicoNome?.trim() || "Profissional não informado",
    type: tipo,
    status: normalizeStatus(consulta.status),
    start,
    end,
    notes: consulta.motivo ?? "",
    room: tipo === "TELECONSULTA" ? "Teleconsulta" : "Sala 01",
    phone: "",
    linkAcesso: consulta.linkAcesso,
  };
}

export function toConsultaTodayItemViewModel(consulta: ConsultaApi): ConsultaTodayItemViewModel {
  const row = toConsultaRowViewModel(consulta);

  return {
    id: row.id,
    hora: formatTime(consulta.dataHora),
    paciente: row.pacienteNome,
    profissional: row.medicoNome,
    status: row.statusLabel,
  };
}
