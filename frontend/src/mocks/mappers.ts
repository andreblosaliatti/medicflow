// src/mocks/mappers.ts
import { getPacientes, getConsultas } from "./db/storage";
import type { PacienteDTO, ConsultaDTO } from "./db/seed";

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
  // isoLocal esperado: "yyyy-mm-ddTHH:mm"
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
  // sem Intl pra evitar diferenças de ambiente? pode usar Intl tranquilamente também.
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
  hora: string; // "08:00"
  paciente: string;
  profissional: string;
  status: string; // label de UI
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
    .sort(
      (a: ConsultaDTO, b: ConsultaDTO) =>
        new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime()
    );

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
      .sort(
        (a: ConsultaDTO, b: ConsultaDTO) =>
          new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime()
      );

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
  dataHoraLabel: string; // "15/02/2026 08:00"
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
  const consultas = getConsultas();
  const c = consultas.find((x: ConsultaDTO) => x.id === id);
  return c ?? null;
}

export function toConsultasRows(): ConsultaRowModel[] {
  const consultas = getConsultas()
    .slice()
    .sort(
      (a: ConsultaDTO, b: ConsultaDTO) =>
        new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime()
    );

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
// Prontuário (centralizado)
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
  dataHora: string; // ISO
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

// Catálogo opcional de detalhes por consulta.
// Se não tiver entrada, a consulta aparece com arrays vazios e só motivo/data/médico.
const prontuarioDetalhesPorConsultaId: Record<
  string,
  Omit<ProntuarioConsulta, "id" | "dataHora" | "medicoNome" | "motivo">
> = {
  "c-1902": {
    anamnese:
      "Refere dor lombar com piora ao permanecer sentado. Nega febre. Sem irradiação para membros inferiores.",
    exameFisico:
      "Dor à palpação paravertebral lombar. Lasègue negativo. Força preservada.",
    diagnosticos: [
      { codigo: "M54.5", descricao: "Dor lombar baixa" },
      { descricao: "Contratura muscular lombar" },
    ],
    medicacoes: [
      { nome: "Dipirona", dose: "500 mg", posologia: "1 comprimido a cada 8h se dor", duracao: "5 dias" },
      {
        nome: "Ciclobenzaprina",
        dose: "5 mg",
        posologia: "1 comprimido à noite",
        duracao: "7 dias",
        observacoes: "Pode causar sonolência.",
      },
    ],
    exames: [
      { nome: "Raio-X coluna lombar", status: "SOLICITADO" },
      { nome: "Hemograma completo", status: "SOLICITADO" },
    ],
    observacoesGerais: "Orientado alongamento e retorno em 7 dias.",
  },
  "c-1701": {
    diagnosticos: [{ codigo: "I10", descricao: "Hipertensão essencial (primária)" }],
    medicacoes: [
      { nome: "Losartana", dose: "50 mg", posologia: "1 comprimido pela manhã", duracao: "Uso contínuo" },
    ],
    exames: [
      { nome: "Creatinina e ureia", status: "SOLICITADO" },
      { nome: "Potássio sérico", status: "SOLICITADO" },
      { nome: "Eletrocardiograma", status: "SOLICITADO" },
    ],
    observacoesGerais: "Manter medidas de estilo de vida. Retorno em 3 meses.",
  },
  "c-0901": {
    diagnosticos: [{ descricao: "Faringoamigdalite aguda" }],
    medicacoes: [
      { nome: "Amoxicilina", dose: "500 mg", posologia: "1 cápsula a cada 8h", duracao: "10 dias" },
      { nome: "Ibuprofeno", dose: "400 mg", posologia: "1 comprimido a cada 8h se dor/febre", duracao: "3 dias" },
    ],
    exames: [{ nome: "Teste rápido para estreptococo", status: "REALIZADO" }],
    observacoesGerais: "Hidratação e repouso vocal. Retorno se piora.",
  },
};

function emptyDetalhes(): Omit<
  ProntuarioConsulta,
  "id" | "dataHora" | "medicoNome" | "motivo"
> {
  return {
    anamnese: undefined,
    exameFisico: undefined,
    diagnosticos: [],
    medicacoes: [],
    exames: [],
    observacoesGerais: undefined,
  };
}

export function getProntuarioByPacienteId(pacienteId: number): ProntuarioPaciente | null {
  const pacientes = getPacientes();
  const consultas = getConsultas();

  const p = pacientes.find((x) => x.id === pacienteId);
  if (!p) return null;

  const list = consultas
    .filter((c: ConsultaDTO) => c.pacienteId === pacienteId)
    .sort(
      (a: ConsultaDTO, b: ConsultaDTO) =>
        new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime()
    );

  const prontuarioConsultas: ProntuarioConsulta[] = list.map((c: ConsultaDTO) => {
    const detalhes = prontuarioDetalhesPorConsultaId[c.id] ?? emptyDetalhes();

    return {
      id: c.id,
      dataHora: c.dataHora,
      medicoNome: c.medicoNome,
      motivo: c.motivo ?? "—",
      ...detalhes,
      // garante arrays sempre presentes
      diagnosticos: detalhes.diagnosticos ?? [],
      medicacoes: detalhes.medicacoes ?? [],
      exames: detalhes.exames ?? [],
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