import { getConsultas, getPacientes, savePacientes } from "../../mocks/db/storage";
import type { ConsultaDTO } from "../../mocks/db/seed";
import {
  duplicateMedicamento,
  getExamesByPacienteId,
  getMedicamentosByPacienteId,
  seedPrescricoesIfEmpty,
  updateExame,
  type ExameSolicitadoMock,
  type MedicamentoPrescritoMock,
} from "../../mocks/prescricoesStorage";
import { HttpError } from "./types";
import type { HttpRequestConfig, HttpTransport } from "./types";

type LoginRequest = { email: string; senha: string };
type LoginResponse = {
  token: string;
  usuario: {
    id: string;
    medicoId?: string | null;
    nome: string;
    email: string;
    role: "MEDICO" | "SECRETARIA" | "ADMIN";
  };
};

type ExamePatch = Partial<Pick<ExameSolicitadoMock, "status" | "dataColeta" | "dataResultado" | "observacoes">>;

type MockPaciente = ReturnType<typeof getPacientes>[number];

type MockPage<TItem> = {
  content: TItem[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};

function parseUrl(url: string) {
  const [path, query = ""] = url.split("?");
  return {
    path,
    searchParams: new URLSearchParams(query),
  };
}

function matchRoute(pathname: string, pattern: string) {
  const input = pathname.split("/").filter(Boolean);
  const template = pattern.split("/").filter(Boolean);

  if (input.length !== template.length) return null;

  const params: Record<string, string> = {};

  for (let index = 0; index < template.length; index += 1) {
    const current = template[index];
    const value = input[index];

    if (current.startsWith(":")) {
      params[current.slice(1)] = value;
      continue;
    }

    if (current !== value) return null;
  }

  return params;
}

function ok<T>(data: T, delayMs = 80): Promise<T> {
  return new Promise((resolve) => {
    window.setTimeout(() => resolve(data), delayMs);
  });
}

function ensureNumber(value: string, fieldName: string) {
  const parsed = Number(value);
  if (!Number.isFinite(parsed)) {
    throw new HttpError(`Parâmetro inválido: ${fieldName}.`, 400);
  }
  return parsed;
}

function composeNome(paciente: MockPaciente) {
  return `${paciente.nome} ${paciente.sobrenome}`.trim();
}

function sortConsultasDesc(a: ConsultaDTO, b: ConsultaDTO) {
  return new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime();
}

function paginate<TItem>(items: TItem[], searchParams: URLSearchParams): MockPage<TItem> {
  const page = Math.max(0, Number(searchParams.get("page") ?? "0") || 0);
  const size = Math.max(1, Number(searchParams.get("size") ?? String(items.length || 10)) || (items.length || 10));
  const start = page * size;
  const content = items.slice(start, start + size);
  const totalPages = Math.max(1, Math.ceil(items.length / size));

  return {
    content,
    totalElements: items.length,
    totalPages,
    size,
    number: page,
    first: page === 0,
    last: page >= totalPages - 1,
    empty: items.length === 0,
  };
}

function latestConsultaByPaciente(pacienteId: number) {
  return getConsultas()
    .filter((consulta) => consulta.pacienteId === pacienteId)
    .sort(sortConsultasDesc)[0] ?? null;
}

function pacienteListItem(paciente: MockPaciente) {
  return {
    id: paciente.id,
    nomeCompleto: composeNome(paciente),
    cpf: paciente.cpf,
    telefone: paciente.telefone,
    planoSaude: paciente.planoSaude || null,
    ativo: paciente.ativo,
    ultimaConsulta: latestConsultaByPaciente(paciente.id)?.dataHora ?? null,
  };
}

function handleLogin(body: LoginRequest | undefined): Promise<LoginResponse> {
  if (!body?.email?.includes("@")) {
    throw new HttpError("E-mail inválido.", 400);
  }

  if (!body.senha || body.senha.length < 4) {
    throw new HttpError("Senha inválida.", 400);
  }

  return ok({
    token: "mock-jwt-token",
    usuario: {
      id: "u1",
      medicoId: "1",
      nome: "Dr. João Carvalho",
      email: body.email,
      role: "MEDICO",
    },
  });
}

function handlePacientes(searchParams: URLSearchParams) {
  const nome = (searchParams.get("nome") ?? "").trim().toLowerCase();
  const cpf = (searchParams.get("cpf") ?? "").trim();
  const ativo = searchParams.get("ativo");
  const convenio = (searchParams.get("convenio") ?? "").trim().toLowerCase();

  const filtered = getPacientes()
    .filter((paciente) => {
      if (nome && !composeNome(paciente).toLowerCase().includes(nome)) return false;
      if (cpf && !paciente.cpf.includes(cpf)) return false;
      if (ativo !== null && String(paciente.ativo) !== ativo) return false;
      if (convenio && !(paciente.planoSaude ?? "").toLowerCase().includes(convenio)) return false;
      return true;
    })
    .sort((a, b) => composeNome(a).localeCompare(composeNome(b)))
    .map(pacienteListItem);

  return ok(paginate(filtered, searchParams));
}

function handlePacienteById(pathname: string) {
  const params = matchRoute(pathname, "/pacientes/:pacienteId");
  if (!params) return null;

  const paciente = getPacientes().find((item) => item.id === ensureNumber(params.pacienteId, "pacienteId"));
  if (!paciente) {
    throw new HttpError("Paciente não encontrado.", 404);
  }

  return ok(paciente);
}

function handlePacientePerfil(pathname: string) {
  const params = matchRoute(pathname, "/pacientes/:pacienteId/perfil");
  if (!params) return null;

  seedPrescricoesIfEmpty();
  const pacienteId = ensureNumber(params.pacienteId, "pacienteId");
  const paciente = getPacientes().find((item) => item.id === pacienteId);
  if (!paciente) {
    throw new HttpError("Paciente não encontrado.", 404);
  }

  const consultas = getConsultas().filter((consulta) => consulta.pacienteId === pacienteId).sort(sortConsultasDesc);
  const ultimaConsulta = consultas[0] ?? null;

  return ok({
    id: paciente.id,
    nomeCompleto: composeNome(paciente),
    cpf: paciente.cpf,
    dataNascimento: paciente.dataNascimento,
    telefone: paciente.telefone,
    email: paciente.email,
    sexo: paciente.sexo,
    planoSaude: paciente.planoSaude || null,
    ativo: paciente.ativo,
    endereco: paciente.endereco,
    historico: {
      totalConsultas: consultas.length,
      totalExamesSolicitados: getExamesByPacienteId(pacienteId).length,
      totalMedicamentosPrescritos: getMedicamentosByPacienteId(pacienteId).length,
      dataUltimaConsulta: ultimaConsulta?.dataHora ?? null,
      ultimaConsulta: ultimaConsulta
        ? {
            id: Number(String(ultimaConsulta.id).replace(/\D/g, "")) || 0,
            dataHora: ultimaConsulta.dataHora,
            tipo: ultimaConsulta.tipo,
            status: ultimaConsulta.status,
            motivo: ultimaConsulta.motivo,
            medicoNome: ultimaConsulta.medicoNome,
          }
        : null,
    },
  });
}

function handleCreatePaciente(body: Partial<MockPaciente> | undefined) {
  if (!body) throw new HttpError("Corpo da requisição é obrigatório.", 400);

  const pacientes = getPacientes();
  const nextId = pacientes.reduce((highest, paciente) => Math.max(highest, paciente.id), 0) + 1;
  const created: MockPaciente = {
    id: nextId,
    nome: body.nome ?? "",
    sobrenome: body.sobrenome ?? "",
    cpf: body.cpf ?? "",
    dataNascimento: body.dataNascimento ?? "",
    telefone: body.telefone ?? "",
    email: body.email ?? "",
    sexo: body.sexo ?? "NAO_INFORMAR",
    endereco: body.endereco ?? {
      logradouro: "",
      numero: "",
      complemento: "",
      bairro: "",
      cidade: "",
      uf: "",
      cep: "",
    },
    planoSaude: body.planoSaude ?? "",
    ativo: body.ativo ?? true,
  };

  savePacientes([...pacientes, created]);
  return ok(created);
}

function handleUpdatePaciente(pathname: string, body: Partial<MockPaciente> | undefined) {
  const params = matchRoute(pathname, "/pacientes/:pacienteId");
  if (!params) return null;
  if (!body) throw new HttpError("Corpo da requisição é obrigatório.", 400);

  const pacienteId = ensureNumber(params.pacienteId, "pacienteId");
  const pacientes = getPacientes();
  const index = pacientes.findIndex((item) => item.id === pacienteId);

  if (index < 0) {
    throw new HttpError("Paciente não encontrado.", 404);
  }

  const updated: MockPaciente = {
    ...pacientes[index],
    nome: body.nome ?? pacientes[index].nome,
    sobrenome: body.sobrenome ?? pacientes[index].sobrenome,
    dataNascimento: body.dataNascimento ?? pacientes[index].dataNascimento,
    telefone: body.telefone ?? pacientes[index].telefone,
    email: body.email ?? pacientes[index].email,
    sexo: body.sexo ?? pacientes[index].sexo,
    endereco: body.endereco ?? pacientes[index].endereco,
    planoSaude: body.planoSaude ?? pacientes[index].planoSaude,
  };

  const next = [...pacientes];
  next[index] = updated;
  savePacientes(next);
  return ok(updated);
}

function handleConsultas() {
  const consultas = getConsultas().sort(sortConsultasDesc);
  return ok(paginate(consultas, new URLSearchParams(`size=${consultas.length || 20}`)));
}

function handleConsultasTabela(searchParams: URLSearchParams) {
  const pacienteId = searchParams.get("pacienteId");

  const filtered = getConsultas()
    .filter((consulta) => !pacienteId || consulta.pacienteId === Number(pacienteId))
    .sort(sortConsultasDesc)
    .map((consulta) => ({
      id: Number(String(consulta.id).replace(/\D/g, "")) || 0,
      dataHora: consulta.dataHora,
      tipo: consulta.tipo,
      status: consulta.status,
      meioPagamento: consulta.meioPagamento,
      valorConsulta: consulta.valorConsulta,
      pago: consulta.pago,
      duracaoMinutos: consulta.duracaoMinutos,
      pacienteId: consulta.pacienteId,
      pacienteNome: composeNome(getPacientes().find((paciente) => paciente.id === consulta.pacienteId) ?? {
        nome: "Paciente",
        sobrenome: "",
      } as MockPaciente),
      medicoId: Number(consulta.medicoId),
      medicoNome: consulta.medicoNome,
      motivo: consulta.motivo,
    }));

  return ok(paginate(filtered, searchParams));
}

function handleMedicamentosByPaciente(pathname: string): Promise<MedicamentoPrescritoMock[]> | null {
  const params = matchRoute(pathname, "/pacientes/:pacienteId/medicamentos");
  if (!params) return null;

  seedPrescricoesIfEmpty();
  return ok(getMedicamentosByPacienteId(ensureNumber(params.pacienteId, "pacienteId")));
}

function handleDuplicateMedicamento(pathname: string): Promise<MedicamentoPrescritoMock> | null {
  const params = matchRoute(pathname, "/medicamentos/:medicamentoId/duplicate");
  if (!params) return null;

  seedPrescricoesIfEmpty();
  const duplicated = duplicateMedicamento(ensureNumber(params.medicamentoId, "medicamentoId"));
  if (!duplicated) {
    throw new HttpError("Medicamento não encontrado.", 404);
  }

  return ok(duplicated);
}

function handleExamesByPaciente(pathname: string): Promise<ExameSolicitadoMock[]> | null {
  const params = matchRoute(pathname, "/pacientes/:pacienteId/exames");
  if (!params) return null;

  seedPrescricoesIfEmpty();
  return ok(getExamesByPacienteId(ensureNumber(params.pacienteId, "pacienteId")));
}

function handleUpdateExame(pathname: string, body: ExamePatch | undefined): Promise<ExameSolicitadoMock> | null {
  const params = matchRoute(pathname, "/exames/:exameId");
  if (!params) return null;

  seedPrescricoesIfEmpty();
  const updated = updateExame(ensureNumber(params.exameId, "exameId"), () => body ?? {});
  if (!updated) {
    throw new HttpError("Exame não encontrado.", 404);
  }

  return ok(updated);
}

export const mockTransport: HttpTransport = async <TResponse, TBody>(
  config: HttpRequestConfig<TBody>,
): Promise<TResponse> => {
  const { path, searchParams } = parseUrl(config.url);

  if (config.method === "POST" && path === "/auth/login") {
    return handleLogin(config.body as LoginRequest | undefined) as Promise<TResponse>;
  }

  if (config.method === "GET" && path === "/pacientes") {
    return handlePacientes(searchParams) as Promise<TResponse>;
  }

  if (config.method === "GET" && path === "/consultas") {
    return handleConsultas() as Promise<TResponse>;
  }

  if (config.method === "GET" && path === "/consultas/tabela") {
    return handleConsultasTabela(searchParams) as Promise<TResponse>;
  }

  if (config.method === "GET") {
    const pacienteProfileResponse = handlePacientePerfil(path);
    if (pacienteProfileResponse) return pacienteProfileResponse as Promise<TResponse>;

    const pacienteDetailResponse = handlePacienteById(path);
    if (pacienteDetailResponse) return pacienteDetailResponse as Promise<TResponse>;

    const medicamentosResponse = handleMedicamentosByPaciente(path);
    if (medicamentosResponse) return medicamentosResponse as Promise<TResponse>;

    const examesResponse = handleExamesByPaciente(path);
    if (examesResponse) return examesResponse as Promise<TResponse>;
  }

  if (config.method === "POST" && path === "/pacientes") {
    return handleCreatePaciente(config.body as Partial<MockPaciente> | undefined) as Promise<TResponse>;
  }

  if (config.method === "POST") {
    const duplicateResponse = handleDuplicateMedicamento(path);
    if (duplicateResponse) return duplicateResponse as Promise<TResponse>;
  }

  if (config.method === "PUT") {
    const updatePacienteResponse = handleUpdatePaciente(path, config.body as Partial<MockPaciente> | undefined);
    if (updatePacienteResponse) return updatePacienteResponse as Promise<TResponse>;
  }

  if (config.method === "PATCH") {
    const updateResponse = handleUpdateExame(path, config.body as ExamePatch | undefined);
    if (updateResponse) return updateResponse as Promise<TResponse>;
  }

  throw new HttpError(`Rota mock não implementada: ${config.method} ${config.url}`, 501);
};
