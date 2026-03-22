import { statusLabel } from "../../domain/ui/statusLabel";
import { statusTone } from "../../domain/ui/statusTone";
import type { PacienteApi } from "../pacientes/types";
import type { ConsultaApi, ConsultaRowViewModel } from "./types";

function two(value: number) {
  return String(value).padStart(2, "0");
}

function formatDateTime(iso: string) {
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return "—";
  return `${two(date.getDate())}/${two(date.getMonth() + 1)}/${date.getFullYear()} ${two(date.getHours())}:${two(date.getMinutes())}`;
}

function pacienteNomeById(pacienteId: number, pacientes: PacienteApi[]) {
  const paciente = pacientes.find((item) => item.id === pacienteId);
  return paciente ? `${paciente.primeiroNome} ${paciente.sobrenome}`.trim() : "Paciente";
}

export function toConsultaRowViewModel(
  consulta: ConsultaApi,
  pacientes: PacienteApi[],
): ConsultaRowViewModel {
  return {
    id: consulta.id,
    dataHoraLabel: formatDateTime(consulta.dataHora),
    pacienteId: consulta.pacienteId,
    pacienteNome: pacienteNomeById(consulta.pacienteId, pacientes),
    medicoNome: consulta.medicoNome,
    tipo: consulta.tipo,
    duracaoMinutos: consulta.duracaoMinutos,
    status: consulta.status,
    statusLabel: statusLabel(consulta.status),
    statusTone: statusTone(consulta.status),
  };
}
