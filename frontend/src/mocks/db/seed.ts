import type { StatusConsulta, TipoConsulta, MeioPagamento, DuracaoMinutos  } from "../../domain/enums/statusConsulta";

export type Sexo = "MASCULINO" | "FEMININO" | "OUTRO" | "NAO_INFORMAR";

export type PacienteDTO = {
  id: number;
  primeiroNome: string;
  sobrenome: string;
  cpf: string;
  dataNascimento: string; // yyyy-mm-dd
  telefone: string;
  email: string;
  sexo: Sexo;
  endereco: {
    logradouro: string;
    numero: string;
    complemento: string;
    bairro: string;
    cidade: string;
    uf: string;
    cep: string;
  };
  planoSaude: string;
  ativo: boolean;
};

export type ConsultaDTO = {
  id: string;
  pacienteId: number;
  medicoId: string;
  medicoNome: string;
  dataHora: string; // ISO local "yyyy-mm-ddTHH:mm"
  duracaoMinutos: DuracaoMinutos;
  tipo: TipoConsulta;
  status: StatusConsulta;
  motivo?: string;
  sala?: string;
  telefoneContato?: string;
  valorConsulta?: number;
  pago?: boolean;
  meioPagamento?: MeioPagamento;
};

export const MEDICO_SEED = {
  id: "1",
  nome: "Dr. João Carvalho",
} as const;

export const seedPacientes: PacienteDTO[] = [
  {
    id: 1,
    primeiroNome: "Sofia",
    sobrenome: "Almeida",
    cpf: "111.222.333-44",
    dataNascimento: "1991-02-10",
    telefone: "(51) 99911-2233",
    email: "sofia.almeida@email.com",
    sexo: "FEMININO",
    endereco: {
      logradouro: "Rua Nilo Peçanha",
      numero: "120",
      complemento: "Apto 402",
      bairro: "Petrópolis",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90470-000",
    },
    planoSaude: "Unimed",
    ativo: true,
  },
  {
    id: 2,
    primeiroNome: "Rafael",
    sobrenome: "Lima",
    cpf: "222.333.444-55",
    dataNascimento: "1986-08-22",
    telefone: "(51) 98822-3344",
    email: "rafael.lima@email.com",
    sexo: "MASCULINO",
    endereco: {
      logradouro: "Av. Ipiranga",
      numero: "3100",
      complemento: "",
      bairro: "Jardim Botânico",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90610-000",
    },
    planoSaude: "Amil",
    ativo: true,
  },
  {
    id: 3,
    primeiroNome: "Ana",
    sobrenome: "Souza",
    cpf: "333.444.555-66",
    dataNascimento: "1992-11-20",
    telefone: "(51) 97733-4455",
    email: "ana.souza@email.com",
    sexo: "FEMININO",
    endereco: {
      logradouro: "Av. Carlos Gomes",
      numero: "1400",
      complemento: "Sala 501",
      bairro: "Boa Vista",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90480-000",
    },
    planoSaude: "Particular",
    ativo: true,
  },
  {
    id: 4,
    primeiroNome: "Gabriel",
    sobrenome: "Martins",
    cpf: "444.555.666-77",
    dataNascimento: "1979-04-03",
    telefone: "(51) 96644-5566",
    email: "gabriel.martins@email.com",
    sexo: "MASCULINO",
    endereco: {
      logradouro: "Rua Padre Chagas",
      numero: "55",
      complemento: "",
      bairro: "Moinhos de Vento",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90570-080",
    },
    planoSaude: "Bradesco Saúde",
    ativo: true,
  },
  {
    id: 5,
    primeiroNome: "Mariana",
    sobrenome: "Oliveira",
    cpf: "555.666.777-88",
    dataNascimento: "1983-12-15",
    telefone: "(51) 95555-6677",
    email: "mariana.oliveira@email.com",
    sexo: "FEMININO",
    endereco: {
      logradouro: "Rua da Praia",
      numero: "900",
      complemento: "Apto 1101",
      bairro: "Centro Histórico",
      cidade: "Porto Alegre",
      uf: "RS",
      cep: "90010-000",
    },
    planoSaude: "SulAmérica",
    ativo: true,
  },
];

// ✅ hoje (pelo teu contexto) é 2026-02-15, então esse seed fica perfeito para simulação.
export const seedConsultas: ConsultaDTO[] = [
  // ======= HOJE =======
  {
    id: "c-2001",
    pacienteId: 1,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-15T08:00",
    duracaoMinutos: 30,
    tipo: "PRESENCIAL",
    status: "CONFIRMADA",
    motivo: "Revisão anual e avaliação geral",
    sala: "Sala 01",
    telefoneContato: "(51) 99911-2233",
    valorConsulta: 250,
    pago: true,
    meioPagamento: "PIX",
  },
  {
    id: "c-2002",
    pacienteId: 2,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-15T09:00",
    duracaoMinutos: 45,
    tipo: "TELECONSULTA",
    status: "AGENDADA",
    motivo: "Cefaleia recorrente há 2 semanas",
    sala: "Teleconsulta",
    telefoneContato: "(51) 98822-3344",
    valorConsulta: 220,
    pago: false,
    meioPagamento: "CARTAO",
  },
  {
    id: "c-2003",
    pacienteId: 3,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-15T10:30",
    duracaoMinutos: 30,
    tipo: "RETORNO",
    status: "AGENDADA",
    motivo: "Avaliar resultados de exames",
    sala: "Sala 02",
    telefoneContato: "(51) 97733-4455",
    valorConsulta: 180,
    pago: false,
    meioPagamento: "PIX",
  },
  {
    id: "c-2004",
    pacienteId: 4,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-15T11:15",
    duracaoMinutos: 30,
    tipo: "PRESENCIAL",
    status: "EM_ATENDIMENTO",
    motivo: "Dor articular e fadiga",
    sala: "Sala 01",
    telefoneContato: "(51) 96644-5566",
    valorConsulta: 280,
    pago: false,
    meioPagamento: "DINHEIRO",
  },
  {
    id: "c-2005",
    pacienteId: 5,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-15T14:00",
    duracaoMinutos: 60,
    tipo: "PRESENCIAL",
    status: "CANCELADA",
    motivo: "Avaliação geral",
    sala: "Sala 01",
    telefoneContato: "(51) 95555-6677",
    valorConsulta: 280,
    pago: false,
    meioPagamento: "DINHEIRO",
  },

  // ======= AMANHÃ / PRÓXIMAS =======
  {
    id: "c-2010",
    pacienteId: 5,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-16T09:15",
    duracaoMinutos: 30,
    tipo: "TELECONSULTA",
    status: "AGENDADA",
    motivo: "Revisão de medicação",
    sala: "Teleconsulta",
    telefoneContato: "(51) 95555-6677",
    valorConsulta: 220,
    pago: false,
    meioPagamento: "PIX",
  },

  // ======= SEMANA / HISTÓRICO RECENTE =======
  {
    id: "c-1901",
    pacienteId: 2,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-13T11:00",
    duracaoMinutos: 30,
    tipo: "PRESENCIAL",
    status: "CONCLUIDA",
    motivo: "Avaliação de pressão arterial",
    sala: "Sala 02",
    telefoneContato: "(51) 98822-3344",
    valorConsulta: 250,
    pago: true,
    meioPagamento: "CARTAO",
  },
  {
    id: "c-1902",
    pacienteId: 1,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-12T14:30",
    duracaoMinutos: 45,
    tipo: "RETORNO",
    status: "CONCLUIDA",
    motivo: "Retorno pós-tratamento",
    sala: "Sala 01",
    telefoneContato: "(51) 99911-2233",
    valorConsulta: 180,
    pago: true,
    meioPagamento: "PIX",
  },
  {
    id: "c-1903",
    pacienteId: 3,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-11T09:45",
    duracaoMinutos: 30,
    tipo: "TELECONSULTA",
    status: "CANCELADA",
    motivo: "Imprevisto do paciente",
    sala: "Teleconsulta",
    telefoneContato: "(51) 97733-4455",
    valorConsulta: 220,
    pago: false,
    meioPagamento: "PIX",
  },
]

export function emptyPaciente(nextId: number): PacienteDTO {
  return {
    id: nextId,
    primeiroNome: "",
    sobrenome: "",
    cpf: "",
    dataNascimento: "",
    telefone: "",
    email: "",
    sexo: "NAO_INFORMAR",
    endereco: {
      logradouro: "",
      numero: "",
      complemento: "",
      bairro: "",
      cidade: "",
      uf: "",
      cep: "",
    },
    planoSaude: "",
    ativo: true,
  };
};