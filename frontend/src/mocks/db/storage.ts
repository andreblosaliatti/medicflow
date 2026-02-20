import { seedPacientes, seedConsultas } from "./seed";
import type { PacienteDTO, ConsultaDTO } from "./seed";

import { seedRegistrosClinicos, type RegistroClinicoDTO } from "./registroClinico.seed";

const LS_PACIENTES = "mf_mock_pacientes_v2";
const LS_CONSULTAS = "mf_mock_consultas_v1";
const LS_REGISTROS = "mf_mock_registros_clinicos_v1";

function readLS<T>(key: string): T[] | null {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as T[];
    return Array.isArray(parsed) ? parsed : null;
  } catch {
    return null;
  }
}

function writeLS<T>(key: string, list: T[]) {
  try {
    localStorage.setItem(key, JSON.stringify(list));
  } catch {
    // ignora
  }
}

// --------------------
// NORMALIZAÇÃO (migração)
// --------------------
function normalizeConsulta(c: Partial<ConsultaDTO>): ConsultaDTO {
  // Se alguém salvou antes de você tornar campos obrigatórios,
  // isso garante que nada vem undefined pro front.
  const id = typeof c.id === "string" && c.id.trim() ? c.id : `c-${Date.now()}`;

  const valorConsulta =
    typeof c.valorConsulta === "number" && Number.isFinite(c.valorConsulta) ? c.valorConsulta : 0;

  const pago = typeof c.pago === "boolean" ? c.pago : false;

  const meioPagamento =
    c.meioPagamento === "PIX" || c.meioPagamento === "CARTAO" || c.meioPagamento === "DINHEIRO"
      ? c.meioPagamento
      : "PIX";

  const dataPagamento = typeof c.dataPagamento === "string" ? c.dataPagamento : "";

  const motivo = typeof c.motivo === "string" ? c.motivo : "";

  // Campos obrigatórios “base”
  if (typeof c.pacienteId !== "number") throw new Error("Consulta inválida: pacienteId ausente");
  if (typeof c.medicoId !== "string") throw new Error("Consulta inválida: medicoId ausente");
  if (typeof c.medicoNome !== "string") throw new Error("Consulta inválida: medicoNome ausente");
  if (typeof c.dataHora !== "string") throw new Error("Consulta inválida: dataHora ausente");
  if (typeof c.duracaoMinutos !== "number") throw new Error("Consulta inválida: duracaoMinutos ausente");
  if (typeof c.tipo !== "string") throw new Error("Consulta inválida: tipo ausente");
  if (typeof c.status !== "string") throw new Error("Consulta inválida: status ausente");

  return {
    id,
    pacienteId: c.pacienteId,
    medicoId: c.medicoId,
    medicoNome: c.medicoNome,
    dataHora: c.dataHora,
    duracaoMinutos: c.duracaoMinutos,
    tipo: c.tipo,
    status: c.status,

    motivo,

    valorConsulta,
    pago,
    meioPagamento,
    dataPagamento,

    sala: c.sala,
    telefoneContato: c.telefoneContato,
  };
}

// --------------------
// Pacientes (compat)
// --------------------
export function getPacientes(): PacienteDTO[] {
  const ls = readLS<PacienteDTO>(LS_PACIENTES);
  if (ls && ls.length) return [...ls];
  writeLS(LS_PACIENTES, seedPacientes);
  return [...seedPacientes];
}

export function savePacientes(list: PacienteDTO[]) {
  writeLS(LS_PACIENTES, list);
}

// --------------------
// Consultas (compat)
// --------------------
export function getConsultas(): ConsultaDTO[] {
  const ls = readLS<Partial<ConsultaDTO>>(LS_CONSULTAS);

  if (ls && ls.length) {
    // ✅ migra/normaliza e salva de volta
    const normalized = ls.map((c) => normalizeConsulta(c));
    writeLS(LS_CONSULTAS, normalized);
    return [...normalized];
  }

  // primeira carga
  writeLS(LS_CONSULTAS, seedConsultas);
  return [...seedConsultas];
}

export function saveConsultas(list: ConsultaDTO[]) {
  writeLS(LS_CONSULTAS, list);
}

export function getConsultaById(id: string): ConsultaDTO | null {
  const list = getConsultas();
  return list.find((c) => c.id === id) ?? null;
}

export function updateConsulta(id: string, patch: Partial<ConsultaDTO>): ConsultaDTO | null {
  const list = getConsultas();
  const idx = list.findIndex((c) => c.id === id);
  if (idx < 0) return null;

  const merged = { ...list[idx], ...patch };
  const updated = normalizeConsulta(merged);

  const next = [...list];
  next[idx] = updated;
  saveConsultas(next);
  return updated;
}

// Mantém compat com o que você já tinha, mas garante normalização
export function addConsulta(dto: ConsultaDTO): ConsultaDTO {
  const normalized = normalizeConsulta(dto);
  const next = [normalized, ...getConsultas()];
  saveConsultas(next);
  return normalized;
}

// --------------------
// Registros Clínicos
// --------------------
export function getRegistrosClinicos(): RegistroClinicoDTO[] {
  const ls = readLS<RegistroClinicoDTO>(LS_REGISTROS);
  if (ls && ls.length) return [...ls];
  writeLS(LS_REGISTROS, seedRegistrosClinicos);
  return [...seedRegistrosClinicos];
}

export function saveRegistrosClinicos(list: RegistroClinicoDTO[]) {
  writeLS(LS_REGISTROS, list);
}

export function getRegistroClinicoByConsultaId(consultaId: string): RegistroClinicoDTO | null {
  const list = getRegistrosClinicos();
  return list.find((r) => r.consultaId === consultaId) ?? null;
}

export function ensureRegistroClinico(consultaId: string): RegistroClinicoDTO {
  const existing = getRegistroClinicoByConsultaId(consultaId);
  if (existing) return existing;

  const novo: RegistroClinicoDTO = {
    consultaId,
    anamnese: "",
    exameFisico: "",
    diagnostico: "",
    prescricaoTexto: "",
    medicacoes: [],
    exames: [],
    observacoes: "",
    updatedAt: new Date().toISOString(),
  };

  const next = [novo, ...getRegistrosClinicos()];
  saveRegistrosClinicos(next);
  return novo;
}

export function upsertRegistroClinico(dto: RegistroClinicoDTO): RegistroClinicoDTO {
  const list = getRegistrosClinicos();
  const idx = list.findIndex((r) => r.consultaId === dto.consultaId);

  const updated: RegistroClinicoDTO = {
    ...dto,
    updatedAt: new Date().toISOString(),
  };

  if (idx < 0) {
    saveRegistrosClinicos([updated, ...list]);
    return updated;
  }

  const next = [...list];
  next[idx] = updated;
  saveRegistrosClinicos(next);
  return updated;
}