import { seedPacientes, seedConsultas } from "./seed";
import type { PacienteDTO, ConsultaDTO } from "./seed";

const LS_PACIENTES = "mf_mock_pacientes_v2";
const LS_CONSULTAS = "mf_mock_consultas_v1";

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

export function getPacientes(): PacienteDTO[] {
  const ls = readLS<PacienteDTO>(LS_PACIENTES);
  if (ls && ls.length) return [...ls];
  writeLS(LS_PACIENTES, seedPacientes);
  return [...seedPacientes];
}

export function savePacientes(list: PacienteDTO[]) {
  writeLS(LS_PACIENTES, list);
}

export function getConsultas(): ConsultaDTO[] {
  const ls = readLS<ConsultaDTO>(LS_CONSULTAS);
  if (ls && ls.length) return [...ls];
  writeLS(LS_CONSULTAS, seedConsultas);
  return [...seedConsultas];
}

export function saveConsultas(list: ConsultaDTO[]) {
  writeLS(LS_CONSULTAS, list);
}