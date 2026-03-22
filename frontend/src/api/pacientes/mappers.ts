import type { ConsultaApi } from "../consultas/types";
import type { PacienteApi, PacienteRowViewModel } from "./types";

function two(value: number) {
  return String(value).padStart(2, "0");
}

function formatDate(iso: string) {
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return "Nunca";
  return `${two(date.getDate())}/${two(date.getMonth() + 1)}/${date.getFullYear()}`;
}

function initials(nomeCompleto: string) {
  return nomeCompleto
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? "")
    .join("");
}

export function toPacienteRowViewModel(
  paciente: PacienteApi,
  consultas: ConsultaApi[],
): PacienteRowViewModel {
  const nome = `${paciente.primeiroNome} ${paciente.sobrenome}`.trim();
  const ultimaConsulta = consultas
    .filter((consulta) => consulta.pacienteId === paciente.id)
    .sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime())[0];

  return {
    id: paciente.id,
    nome,
    telefone: paciente.telefone,
    ultimaConsulta: ultimaConsulta ? formatDate(ultimaConsulta.dataHora) : "Nunca",
    convenio: paciente.planoSaude || "Nunca",
    initials: initials(nome),
  };
}
