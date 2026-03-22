import { getConsultas, getPacientes } from "../../mocks/db/storage";
import type { ConsultaDTO, PacienteDTO } from "../../mocks/db/seed";
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
    nome: string;
    email: string;
    role: "MEDICO" | "SECRETARIA" | "ADMIN";
  };
};

type ExamePatch = Partial<Pick<ExameSolicitadoMock, "status" | "dataColeta" | "dataResultado" | "observacoes">>;

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

  for (let i = 0; i < template.length; i += 1) {
    const current = template[i];
    const value = input[i];

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
      nome: "Dr. João Carvalho",
      email: body.email,
      role: "MEDICO",
    },
  });
}

function handlePacientes(): Promise<PacienteDTO[]> {
  return ok(getPacientes());
}

function handleConsultas(): Promise<ConsultaDTO[]> {
  return ok(getConsultas());
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
  const { path } = parseUrl(config.url);

  if (config.method === "POST" && path === "/auth/login") {
    return handleLogin(config.body as LoginRequest | undefined) as Promise<TResponse>;
  }

  if (config.method === "GET" && path === "/pacientes") {
    return handlePacientes() as Promise<TResponse>;
  }

  if (config.method === "GET" && path === "/consultas") {
    return handleConsultas() as Promise<TResponse>;
  }

  if (config.method === "GET") {
    const medicamentosResponse = handleMedicamentosByPaciente(path);
    if (medicamentosResponse) return medicamentosResponse as Promise<TResponse>;

    const examesResponse = handleExamesByPaciente(path);
    if (examesResponse) return examesResponse as Promise<TResponse>;
  }

  if (config.method === "POST") {
    const duplicateResponse = handleDuplicateMedicamento(path);
    if (duplicateResponse) return duplicateResponse as Promise<TResponse>;
  }

  if (config.method === "PATCH") {
    const updateResponse = handleUpdateExame(path, config.body as ExamePatch | undefined);
    if (updateResponse) return updateResponse as Promise<TResponse>;
  }

  throw new HttpError(`Rota mock não implementada: ${config.method} ${config.url}`, 501);
};
