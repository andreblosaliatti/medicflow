import type { Paciente } from "./types";

const LS_KEY = "mf_mock_pacientes_v1";

const seed: PacienteDTO[] = [
  {
    id: 5,
    primeiroNome: "Pedro",
    sobrenome: "Santos",
    cpf: "123.456.789-00",
    dataNascimento: "1988-05-12",
    telefone: "(51) 99999-1111",
    email: "pedro@email.com",
    sexo: "MASCULINO",
    endereco: {
      logradouro: "Rua A",
      numero: "100",
      complemento: "",
      bairro: "Centro",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90000-000",
    },
    planoSaude: "Unimed",
    ativo: true,
  },
  {
    id: 3,
    primeiroNome: "Ana",
    sobrenome: "Souza",
    cpf: "987.654.321-00",
    dataNascimento: "1992-11-20",
    telefone: "(51) 98888-2222",
    email: "ana@email.com",
    sexo: "FEMININO",
    endereco: {
      logradouro: "Av. B",
      numero: "200",
      complemento: "Apto 12",
      bairro: "Petrópolis",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90400-000",
    },
    planoSaude: "Particular",
    ativo: true,
  },
];

function readLS(): PacienteDTO[] | null {
  try {
    const raw = localStorage.getItem(LS_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as PacienteDTO[];
    return Array.isArray(parsed) ? parsed : null;
  } catch {
    return null;
  }
}

function writeLS(list: PacienteDTO[]) {
  try {
    localStorage.setItem(LS_KEY, JSON.stringify(list));
  } catch {
    // ignora
  }
}

export function getAllPacientes(): PacienteDTO[] {
  const ls = readLS();
  if (ls && ls.length > 0) return [...ls];

  // inicializa seed no primeiro acesso
  writeLS(seed);
  return [...seed];
}

export function getPacienteById(id: number): PacienteDTO | null {
  const list = getAllPacientes();
  return list.find((p) => p.id === id) ?? null;
}

export function getNextPacienteId(): number {
  const list = getAllPacientes();
  const max = list.reduce((acc, p) => (p.id > acc ? p.id : acc), 0);
  return max + 1;
}

export function savePaciente(dto: PacienteDTO): PacienteDTO {
  const list = getAllPacientes();
  const idx = list.findIndex((p) => p.id === dto.id);

  const next = idx >= 0
    ? list.map((p) => (p.id === dto.id ? dto : p))
    : [...list, dto];

  writeLS(next);
  return dto;
}
