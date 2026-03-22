import type { PageResponse } from "../shared/types";
import type {
  EnderecoApi,
  PacienteApi,
  PacienteCreatePayload,
  PacienteFormValues,
  PacienteListApi,
  PacienteProfileApi,
  PacienteProfileViewModel,
  PacienteRowViewModel,
  PacienteUpdatePayload,
} from "./types";

function two(value: number) {
  return String(value).padStart(2, "0");
}

function parseDate(value: string | null | undefined) {
  if (!value) return null;
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function formatDate(value: string | null | undefined) {
  const date = parseDate(value);
  if (!date) return "Nunca";
  return `${two(date.getDate())}/${two(date.getMonth() + 1)}/${date.getFullYear()}`;
}

function formatTime(value: string | null | undefined) {
  const date = parseDate(value);
  if (!date) return "—";
  return `${two(date.getHours())}:${two(date.getMinutes())}`;
}

function toTitleCase(value: string | null | undefined) {
  if (!value) return "Não informado";
  return value
    .toLowerCase()
    .split("_")
    .map((part) => (part ? `${part[0].toUpperCase()}${part.slice(1)}` : ""))
    .join(" ");
}

function initials(nomeCompleto: string) {
  return nomeCompleto
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? "")
    .join("");
}

function safeEndereco(endereco: EnderecoApi | null | undefined): EnderecoApi {
  return {
    logradouro: endereco?.logradouro ?? "",
    numero: endereco?.numero ?? "",
    complemento: endereco?.complemento ?? "",
    bairro: endereco?.bairro ?? "",
    cidade: endereco?.cidade ?? "",
    uf: endereco?.uf ?? "",
    cep: endereco?.cep ?? "",
  };
}

function calculateAge(value: string) {
  const birthDate = parseDate(value);
  if (!birthDate) return "Idade não informada";

  const now = new Date();
  let age = now.getFullYear() - birthDate.getFullYear();
  const monthDiff = now.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birthDate.getDate())) {
    age -= 1;
  }

  return `${Math.max(age, 0)} anos`;
}

function formatAddressCompact(endereco: EnderecoApi | null | undefined) {
  if (!endereco) return "Endereço não informado";

  const line1 = [endereco.logradouro, endereco.numero].filter(Boolean).join(", ");
  const line2 = [endereco.bairro, [endereco.cidade, endereco.uf].filter(Boolean).join(" - ")]
    .filter(Boolean)
    .join(" • ");

  return [line1, line2].filter(Boolean).join(" · ") || "Endereço não informado";
}

function formatAddressFull(endereco: EnderecoApi | null | undefined) {
  if (!endereco) return "Endereço não informado";

  const parts = [
    [endereco.logradouro, endereco.numero].filter(Boolean).join(", "),
    endereco.complemento,
    endereco.bairro,
    [endereco.cidade, endereco.uf].filter(Boolean).join(" - "),
    endereco.cep ? `CEP ${endereco.cep}` : "",
  ].filter(Boolean);

  return parts.join(" • ") || "Endereço não informado";
}

export function emptyPacienteForm(): PacienteFormValues {
  return {
    id: 0,
    primeiroNome: "",
    sobrenome: "",
    cpf: "",
    dataNascimento: "",
    telefone: "",
    email: "",
    sexo: "NAO_INFORMAR",
    endereco: safeEndereco(null),
    planoSaude: "",
    ativo: true,
  };
}

export function toPacienteCreatePayload(values: PacienteFormValues): PacienteCreatePayload {
  return {
    primeiroNome: values.primeiroNome.trim(),
    sobrenome: values.sobrenome.trim(),
    cpf: values.cpf.trim(),
    dataNascimento: values.dataNascimento,
    telefone: values.telefone.trim(),
    email: values.email.trim(),
    sexo: values.sexo,
    endereco: safeEndereco(values.endereco),
    planoSaude: values.planoSaude.trim(),
    ativo: values.ativo,
  };
}

export function toPacienteUpdatePayload(values: PacienteFormValues): PacienteUpdatePayload {
  return {
    primeiroNome: values.primeiroNome.trim(),
    sobrenome: values.sobrenome.trim(),
    telefone: values.telefone.trim(),
    email: values.email.trim(),
    sexo: values.sexo,
    dataNascimento: values.dataNascimento,
    endereco: safeEndereco(values.endereco),
    planoSaude: values.planoSaude.trim(),
  };
}

export function toPacienteFormValues(api: PacienteApi): PacienteFormValues {
  return {
    ...api,
    endereco: safeEndereco(api.endereco),
    planoSaude: api.planoSaude ?? "",
  };
}

export function toPacienteRowViewModel(paciente: PacienteListApi): PacienteRowViewModel {
  return {
    id: paciente.id,
    nome: paciente.nomeCompleto,
    cpf: paciente.cpf,
    telefone: paciente.telefone,
    ultimaConsulta: formatDate(paciente.ultimaConsulta),
    convenio: paciente.planoSaude?.trim() || "Nunca",
    initials: initials(paciente.nomeCompleto),
    ativo: paciente.ativo,
  };
}

export function toPacienteRows(page: PageResponse<PacienteListApi>): PacienteRowViewModel[] {
  return page.content.map(toPacienteRowViewModel);
}

export function toPacienteProfileViewModel(api: PacienteProfileApi): PacienteProfileViewModel {
  const ultimaConsulta = api.historico.ultimaConsulta;

  return {
    id: api.id,
    nomeCompleto: api.nomeCompleto,
    initials: initials(api.nomeCompleto),
    cpf: api.cpf,
    idadeLabel: calculateAge(api.dataNascimento),
    dataNascimentoLabel: formatDate(api.dataNascimento),
    telefone: api.telefone || "Não informado",
    email: api.email || "Não informado",
    sexo: toTitleCase(api.sexo),
    planoSaude: api.planoSaude?.trim() || "Não informado",
    ativo: api.ativo,
    enderecoCompacto: formatAddressCompact(api.endereco),
    enderecoCompleto: formatAddressFull(api.endereco),
    historico: {
      totalConsultas: api.historico.totalConsultas,
      totalExamesSolicitados: api.historico.totalExamesSolicitados,
      totalMedicamentosPrescritos: api.historico.totalMedicamentosPrescritos,
      dataUltimaConsultaLabel: formatDate(api.historico.dataUltimaConsulta),
      ultimaConsulta: ultimaConsulta
        ? {
            id: ultimaConsulta.id,
            dataLabel: formatDate(ultimaConsulta.dataHora),
            horaLabel: formatTime(ultimaConsulta.dataHora),
            medicoNome: ultimaConsulta.medicoNome?.trim() || "Profissional não informado",
            statusLabel: toTitleCase(ultimaConsulta.status),
            tipoLabel: toTitleCase(ultimaConsulta.tipo),
            motivo: ultimaConsulta.motivo?.trim() || "Motivo não informado",
          }
        : null,
    },
  };
}
