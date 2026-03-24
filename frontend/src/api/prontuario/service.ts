import { getConsultaDetails, listConsultasApi } from "../consultas/service";
import type { ConsultaTableItemApi } from "../consultas/types";
import { listExamesByPacienteId } from "../exames/service";
import type { ExameViewModel } from "../exames/types";
import { listMedicamentosByPacienteId } from "../medicamentos/service";
import type { MedicamentoViewModel } from "../medicamentos/types";
import { getPacienteById } from "../pacientes/service";
import type { ProntuarioConsulta, ProntuarioPaciente } from "./types";

// Estratégia adotada: composição via múltiplas chamadas.
// Neste estágio, já existem endpoints por domínio (consultas, medicamentos e exames),
// então reutilizamos esses contratos e evitamos introduzir um endpoint agregado adicional.
type ConsultaDetailLite = {
  anamnese: string | null;
  exameFisico: string | null;
  observacoes: string | null;
  diagnostico: string | null;
};

function safeText(value: string | null | undefined, fallback = "—") {
  const normalized = value?.trim();
  return normalized && normalized.length > 0 ? normalized : fallback;
}

function toDiagnosticos(diagnostico: string | null | undefined) {
  if (!diagnostico?.trim()) return [];
  return [{ descricao: diagnostico.trim() }];
}

function mapConsultaToProntuario(
  consulta: ConsultaTableItemApi,
  consultaDetail: ConsultaDetailLite,
  medicamentos: MedicamentoViewModel[],
  exames: ExameViewModel[],
): ProntuarioConsulta {
  return {
    id: String(consulta.id),
    dataHora: consulta.dataHora,
    medicoNome: safeText(consulta.medicoNome, "Profissional não informado"),
    motivo: safeText(consulta.motivo),
    anamnese: consultaDetail.anamnese ?? undefined,
    exameFisico: consultaDetail.exameFisico ?? undefined,
    observacoes: consultaDetail.observacoes ?? undefined,
    diagnosticos: toDiagnosticos(consultaDetail.diagnostico),
    medicacoes: medicamentos.map((medicamento) => ({
      nome: medicamento.nome,
      dose: medicamento.dosagem || undefined,
      posologia: [medicamento.frequencia, medicamento.via].filter(Boolean).join(" • ") || "—",
    })),
    exames: exames.map((exame) => ({
      nome: exame.nome,
      status: exame.status,
    })),
  };
}

export async function getProntuarioByPacienteId(pacienteId: number): Promise<ProntuarioPaciente | null> {
  const [paciente, consultasPage, medicamentos, exames] = await Promise.all([
    getPacienteById(pacienteId),
    listConsultasApi({ pacienteId, size: 100, sort: "dataHora,desc" }),
    listMedicamentosByPacienteId(pacienteId),
    listExamesByPacienteId(pacienteId),
  ]);

  const detalhesConsultas = await Promise.all(
    consultasPage.content.map((consulta) => getConsultaDetails(Number(consulta.id))),
  );

  const detailByConsultaId = new Map(detalhesConsultas.map((item) => [Number(item.id), item]));

  const consultas = consultasPage.content
    .slice()
    .sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime())
    .map((consulta) => {
      const consultaId = Number(consulta.id);
      const medsPorConsulta = medicamentos.filter((m) => m.consultaId === consultaId);
      const examesPorConsulta = exames.filter((e) => e.consultaId === consultaId);
      const detalheConsulta = detailByConsultaId.get(consultaId);
      if (!detalheConsulta) {
        return mapConsultaToProntuario(consulta, { anamnese: null, exameFisico: null, observacoes: null, diagnostico: null }, medsPorConsulta, examesPorConsulta);
      }

      return mapConsultaToProntuario(consulta, detalheConsulta, medsPorConsulta, examesPorConsulta);
    });

  return {
    pacienteId: paciente.id,
    pacienteNome: `${paciente.primeiroNome} ${paciente.sobrenome}`.trim(),
    documento: paciente.cpf,
    dataNascimento: paciente.dataNascimento,
    consultas,
  };
}
