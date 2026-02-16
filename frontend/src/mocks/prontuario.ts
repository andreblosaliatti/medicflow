// src/mocks/prontuario.ts
import { getPacientes, getConsultas } from "./db/storage";
import type { ConsultaDTO, PacienteDTO } from "./db/seed";

export type ProntuarioDiagnostico = {
  codigo?: string;
  descricao: string;
};

export type ProntuarioMedicacao = {
  nome: string;
  dose?: string;
  posologia: string;
  duracao?: string;
};

export type ProntuarioExame = {
  nome: string;
  status: "SOLICITADO" | "REALIZADO" | "RESULTADO";
};

export type ProntuarioConsulta = {
  id: string;
  dataHora: string;
  medicoNome: string;
  motivo: string;

  anamnese?: string;

  diagnosticos: ProntuarioDiagnostico[];
  medicacoes: ProntuarioMedicacao[];
  exames: ProntuarioExame[];
    observacoes?: string;
};

export type ProntuarioPaciente = {
  pacienteId: number;
  pacienteNome: string;
  documento?: string;
  dataNascimento?: string;
  consultas: ProntuarioConsulta[];
};

function nomeCompleto(p: PacienteDTO) {
  return `${p.primeiroNome} ${p.sobrenome}`.trim();
}

function baseFromConsulta(c: ConsultaDTO): ProntuarioConsulta {
  return {
    id: c.id,
    dataHora: c.dataHora,
    medicoNome: c.medicoNome,
    motivo: c.motivo ?? "Consulta clínica",

    anamnese:
      "Paciente refere sintomas iniciados há alguns dias. Nega alergias conhecidas. Histórico prévio sem alterações relevantes.",

    diagnosticos: [
      {
        codigo: "J06.9",
        descricao: "Infecção das vias aéreas superiores, não especificada",
      },
    ],

    medicacoes: [
      {
        nome: "Paracetamol",
        dose: "500mg",
        posologia: "1 comprimido a cada 8 horas",
        duracao: "5 dias",
      },
    ],

    exames: [
      {
        nome: "Hemograma completo",
        status: "SOLICITADO",
      },
    ],
  };
}

export function getProntuarioByPacienteId(
  pacienteId: number
): ProntuarioPaciente | null {
  const pacientes = getPacientes();
  const paciente = pacientes.find((p) => p.id === pacienteId);
  if (!paciente) return null;

  const consultas = getConsultas()
    .filter((c) => c.pacienteId === pacienteId)
    .sort(
      (a, b) =>
        new Date(b.dataHora).getTime() -
        new Date(a.dataHora).getTime()
    )
    .map(baseFromConsulta);

  return {
    pacienteId: paciente.id,
    pacienteNome: nomeCompleto(paciente),
    documento: paciente.cpf,
    dataNascimento: paciente.dataNascimento,
    consultas,
  };
}