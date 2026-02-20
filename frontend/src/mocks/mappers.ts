// src/mocks/mappers.ts
import {
  getPacientes,
  getConsultas,
  getConsultaById as getConsultaByIdFromStorage,
  updateConsulta,
  ensureRegistroClinico,
  getRegistroClinicoByConsultaId,
  upsertRegistroClinico,
} from "./db/storage";

import type { PacienteDTO, ConsultaDTO } from "./db/seed";
import type { RegistroClinicoDTO } from "../mocks/db/registroClinico.seed";

import { statusLabel } from "../domain/ui/statusLabel";
import { statusTone } from "../domain/ui/statusTone";

// =========================
// helpers base
// =========================
export function pacienteNomeById(pacienteId: number): string {
  const pacientes: PacienteDTO[] = getPacientes();
  const p = pacientes.find((x) => x.id === pacienteId);
  return p ? `${p.primeiroNome} ${p.sobrenome}` : "Paciente";
}

function two(n: number) {
  return String(n).padStart(2, "0");
}

function ddmmyyyy(isoLocal: string): string {
  const dt = new Date(isoLocal);
  if (Number.isNaN(dt.getTime())) return "—";
  return `${two(dt.getDate())}/${two(dt.getMonth() + 1)}/${dt.getFullYear()}`;
}

function hhmm(isoLocal: string): string {
  const dt = new Date(isoLocal);
  if (Number.isNaN(dt.getTime())) return "—";
  return `${two(dt.getHours())}:${two(dt.getMinutes())}`;
}

function datetimeLabel(isoLocal: string): string {
  const dt = new Date(isoLocal);
  if (Number.isNaN(dt.getTime())) return "—";
  return `${ddmmyyyy(isoLocal)} ${hhmm(isoLocal)}`;
}

function moneyBRL(v?: number): string {
  if (typeof v !== "number" || Number.isNaN(v)) return "—";
  return `R$ ${v.toFixed(2)}`.replace(".", ",");
}

// =========================
// Agenda (UI)
// =========================
export type AppointmentType = "PRESENCIAL" | "TELECONSULTA" | "RETORNO";
export type AppointmentStatus =
  | "AGENDADA"
  | "CONFIRMADA"
  | "EM_ATENDIMENTO"
  | "CONCLUIDA"
  | "CANCELADA";

export type AppointmentEvent = {
  id: string;
  patientName: string;
  professionalName: string;
  type: AppointmentType;
  status: AppointmentStatus;
  start: Date;
  end: Date;
  room?: string;
  phone?: string;
  notes?: string;
};

export function toAppointmentEvents(): AppointmentEvent[] {
  const consultas = getConsultas();

  return consultas.map((c: ConsultaDTO) => {
    const start = new Date(c.dataHora);
    const end = new Date(start.getTime() + c.duracaoMinutos * 60000);

    return {
      id: c.id,
      patientName: pacienteNomeById(c.pacienteId),
      professionalName: c.medicoNome,
      type: c.tipo,
      status: c.status,
      start,
      end,
      room: c.sala ?? (c.tipo === "TELECONSULTA" ? "Teleconsulta" : "Sala 01"),
      phone: c.telefoneContato ?? "",
      notes: c.motivo ?? "",
    };
  });
}

// =========================
// Dashboard (tabela Agenda Hoje)
// =========================
export type ConsultaItemModel = {
  id: string;
  hora: string;
  paciente: string;
  profissional: string;
  status: string;
};

export function toConsultasHojeItems(): ConsultaItemModel[] {
  const hoje = new Date();
  const y = hoje.getFullYear();
  const m = hoje.getMonth();
  const d = hoje.getDate();

  const consultas = getConsultas()
    .filter((c: ConsultaDTO) => {
      const dt = new Date(c.dataHora);
      return dt.getFullYear() === y && dt.getMonth() === m && dt.getDate() === d;
    })
    .sort((a: ConsultaDTO, b: ConsultaDTO) => new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime());

  return consultas.map((c: ConsultaDTO) => ({
    id: c.id,
    hora: hhmm(c.dataHora),
    paciente: pacienteNomeById(c.pacienteId),
    profissional: c.medicoNome,
    status: statusLabel(c.status),
  }));
}

// =========================
// PacientesPage
// =========================
export type PacienteRowModel = {
  id: number;
  nome: string;
  telefone: string;
  ultimaConsulta: string;
  convenio: string;
};

export function toPacientesRows(): PacienteRowModel[] {
  const pacientes = getPacientes();
  const consultas = getConsultas();

  function ultimaConsulta(pacienteId: number): string {
    const list = consultas
      .filter((c: ConsultaDTO) => c.pacienteId === pacienteId)
      .sort((a: ConsultaDTO, b: ConsultaDTO) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime());

    if (!list.length) return "Nunca";
    return ddmmyyyy(list[0].dataHora);
  }

  return pacientes.map((p: PacienteDTO) => ({
    id: p.id,
    nome: `${p.primeiroNome} ${p.sobrenome}`.trim(),
    telefone: p.telefone,
    ultimaConsulta: ultimaConsulta(p.id),
    convenio: p.planoSaude || "Nunca",
  }));
}

// =========================
// ConsultasPage (listagem geral + detalhes)
// =========================
export type ConsultaRowModel = {
  id: string;
  dataHoraLabel: string;
  pacienteId: number;
  pacienteNome: string;
  medicoNome: string;
  tipo: ConsultaDTO["tipo"];
  duracaoMinutos: ConsultaDTO["duracaoMinutos"];
  status: ConsultaDTO["status"];
  statusLabel: string;
  statusTone: ReturnType<typeof statusTone>;
  motivo: string;
  sala?: string;
  telefoneContato?: string;
  valorConsultaLabel: string;
  pagoLabel: string;
  meioPagamentoLabel: string;
};

export function getConsultaById(id: string): ConsultaDTO | null {
  return getConsultaByIdFromStorage(id);
}

export function toConsultasRows(): ConsultaRowModel[] {
  const consultas = getConsultas()
    .slice()
    .sort((a: ConsultaDTO, b: ConsultaDTO) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime());

  return consultas.map((c: ConsultaDTO) => ({
    id: c.id,
    dataHoraLabel: datetimeLabel(c.dataHora),
    pacienteId: c.pacienteId,
    pacienteNome: pacienteNomeById(c.pacienteId),
    medicoNome: c.medicoNome,
    tipo: c.tipo,
    duracaoMinutos: c.duracaoMinutos,
    status: c.status,
    statusLabel: statusLabel(c.status),
    statusTone: statusTone(c.status),
    motivo: c.motivo ?? "—",
    sala: c.sala ?? (c.tipo === "TELECONSULTA" ? "Teleconsulta" : "Sala 01"),
    telefoneContato: c.telefoneContato ?? "—",
    valorConsultaLabel: moneyBRL(c.valorConsulta),
    pagoLabel: c.pago ? "Sim" : "Não",
    meioPagamentoLabel: c.meioPagamento ?? "—",
  }));
}

export type ConsultaDetailsModel = {
  id: string;
  dataHora: string;
  dataHoraLabel: string;
  pacienteId: number;
  pacienteNome: string;
  medicoNome: string;
  tipo: ConsultaDTO["tipo"];
  duracaoMinutos: ConsultaDTO["duracaoMinutos"];
  status: ConsultaDTO["status"];
  statusLabel: string;
  statusTone: ReturnType<typeof statusTone>;
  motivo: string;
  sala?: string;
  telefoneContato?: string;
  valorConsultaLabel: string;
  pagoLabel: string;
  meioPagamentoLabel: string;
};

export function toConsultaDetailsModel(id: string): ConsultaDetailsModel | null {
  const c = getConsultaById(id);
  if (!c) return null;

  return {
    id: c.id,
    dataHora: c.dataHora,
    dataHoraLabel: datetimeLabel(c.dataHora),
    pacienteId: c.pacienteId,
    pacienteNome: pacienteNomeById(c.pacienteId),
    medicoNome: c.medicoNome,
    tipo: c.tipo,
    duracaoMinutos: c.duracaoMinutos,
    status: c.status,
    statusLabel: statusLabel(c.status),
    statusTone: statusTone(c.status),
    motivo: c.motivo ?? "—",
    sala: c.sala ?? (c.tipo === "TELECONSULTA" ? "Teleconsulta" : "Sala 01"),
    telefoneContato: c.telefoneContato ?? "—",
    valorConsultaLabel: moneyBRL(c.valorConsulta),
    pagoLabel: c.pago ? "Sim" : "Não",
    meioPagamentoLabel: c.meioPagamento ?? "—",
  };
}

// =========================
// Atendimento (Registro Clínico) - NOVO
// =========================
export function iniciarAtendimento(consultaId: string): boolean {
  const c = getConsultaById(consultaId);
  if (!c) return false;
  if (c.status === "CANCELADA" || c.status === "CONCLUIDA") return false;

  // garante que exista registro
  ensureRegistroClinico(consultaId);

  // muda status
  updateConsulta(consultaId, { status: "EM_ATENDIMENTO" });
  return true;
}

export function finalizarAtendimento(consultaId: string): boolean {
  const c = getConsultaById(consultaId);
  if (!c) return false;
  if (c.status === "CANCELADA") return false;

  updateConsulta(consultaId, { status: "CONCLUIDA" });
  return true;
}

export function getRegistroClinico(consultaId: string): RegistroClinicoDTO {
  return getRegistroClinicoByConsultaId(consultaId) ?? ensureRegistroClinico(consultaId);
}

export function salvarRegistroClinico(dto: RegistroClinicoDTO): RegistroClinicoDTO {
  return upsertRegistroClinico(dto);
}

// =========================
// Atendimento (model para tela)
// =========================
export type AtendimentoModel = {
  consultaId: string;

  pacienteId: number;
  pacienteNome: string;

  medicoNome: string;
  dataHora: string;
  dataHoraLabel: string;

  tipo: ConsultaDTO["tipo"];
  status: ConsultaDTO["status"];

  registro: RegistroClinicoDTO;
};

export function toAtendimentoModel(consultaId: string): AtendimentoModel | null {
  const c = getConsultaById(consultaId);
  if (!c) return null;

  const registro =
    getRegistroClinicoByConsultaId(consultaId) ?? ensureRegistroClinico(consultaId);

  return {
    consultaId: c.id,
    pacienteId: c.pacienteId,
    pacienteNome: pacienteNomeById(c.pacienteId),
    medicoNome: c.medicoNome,
    dataHora: c.dataHora,
    dataHoraLabel: datetimeLabel(c.dataHora),
    tipo: c.tipo,
    status: c.status,
    registro,
  };
}

// =========================
// Prontuário (agora REAL: vem do Registro Clínico)
// =========================
export type ProntuarioDiagnostico = {
  codigo?: string;
  descricao: string;
};

export type ProntuarioMedicacao = {
  nome: string;
  dose?: string;
  posologia: string;
  duracao?: string;
  observacoes?: string;
};

export type ProntuarioExame = {
  nome: string;
  status: "SOLICITADO" | "REALIZADO" | "RESULTADO";
  observacoes?: string;
};

export type ProntuarioConsulta = {
  id: string;
  dataHora: string;
  medicoNome: string;
  motivo: string;

  anamnese?: string;
  exameFisico?: string;

  diagnosticos: ProntuarioDiagnostico[];
  medicacoes: ProntuarioMedicacao[];
  exames: ProntuarioExame[];

  observacoesGerais?: string;
};

export type ProntuarioPaciente = {
  pacienteId: string;
  pacienteNome: string;
  documento?: string;
  dataNascimento?: string;
  consultas: ProntuarioConsulta[];
};

export function getProntuarioByPacienteId(pacienteId: number): ProntuarioPaciente | null {
  const pacientes = getPacientes();
  const consultas = getConsultas();

  const p = pacientes.find((x) => x.id === pacienteId);
  if (!p) return null;

  const list = consultas
    .filter((c: ConsultaDTO) => c.pacienteId === pacienteId)
    .slice()
    .sort((a: ConsultaDTO, b: ConsultaDTO) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime());

  const prontuarioConsultas: ProntuarioConsulta[] = list.map((c: ConsultaDTO) => {
    const reg = getRegistroClinicoByConsultaId(c.id);

    // mantém compatibilidade com o teu tipo antigo:
    // diagnosticos vira array (mesmo que venha texto livre)
    const diagnosticos: ProntuarioDiagnostico[] =
      reg?.diagnostico && reg.diagnostico.trim()
        ? [{ descricao: reg.diagnostico.trim() }]
        : [];

    // medicações/exames ainda não estruturados no mock → mantém vazio
    const medicacoes: ProntuarioMedicacao[] = [];
    const exames: ProntuarioExame[] = [];

    // junta observações + prescrição texto no campo que sua UI já sabe renderizar
    const obsParts: string[] = [];
    if (reg?.observacoes?.trim()) obsParts.push(reg.observacoes.trim());
    if (reg?.prescricaoTexto?.trim()) obsParts.push(`Prescrição:\n${reg.prescricaoTexto.trim()}`);

    return {
      id: c.id,
      dataHora: c.dataHora,
      medicoNome: c.medicoNome,
      motivo: c.motivo ?? "—",
      anamnese: reg?.anamnese || undefined,
      exameFisico: reg?.exameFisico || undefined,
      diagnosticos,
      medicacoes,
      exames,
      observacoesGerais: obsParts.length ? obsParts.join("\n\n") : undefined,
    };
  });

  return {
    pacienteId: String(p.id),
    pacienteNome: `${p.primeiroNome} ${p.sobrenome}`.trim(),
    documento: p.cpf,
    dataNascimento: p.dataNascimento,
    consultas: prontuarioConsultas,
  };
}