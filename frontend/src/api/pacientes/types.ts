import type { PageResponse } from "../shared/types";

export type PacienteSexo = "MASCULINO" | "FEMININO" | "OUTRO" | "NAO_INFORMAR";

export type EnderecoApi = {
  logradouro: string;
  numero: string;
  complemento: string;
  bairro: string;
  cidade: string;
  uf: string;
  cep: string;
};

export type PacienteApi = {
  id: number;
  primeiroNome: string;
  sobrenome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  sexo: PacienteSexo;
  endereco: EnderecoApi;
  planoSaude: string;
  ativo: boolean;
};

export type PacienteListApi = {
  id: number;
  nomeCompleto: string;
  cpf: string;
  telefone: string;
  planoSaude: string | null;
  ativo: boolean;
  ultimaConsulta: string | null;
};

export type PacienteListResponse = PageResponse<PacienteListApi>;

export type PacienteUltimaConsultaResumoApi = {
  id: number;
  dataHora: string;
  tipo: string;
  status: string;
  motivo: string | null;
  medicoNome: string | null;
};

export type PacienteHistoricoResumoApi = {
  totalConsultas: number;
  totalExamesSolicitados: number;
  totalMedicamentosPrescritos: number;
  dataUltimaConsulta: string | null;
  ultimaConsulta: PacienteUltimaConsultaResumoApi | null;
};

export type PacienteProfileApi = {
  id: number;
  nomeCompleto: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  sexo: string;
  planoSaude: string | null;
  ativo: boolean;
  endereco: EnderecoApi | null;
  historico: PacienteHistoricoResumoApi;
};

export type PacienteCreatePayload = Omit<PacienteApi, "id">;

export type PacienteUpdatePayload = {
  primeiroNome: string;
  sobrenome: string;
  telefone: string;
  email: string;
  sexo: PacienteSexo;
  dataNascimento: string;
  endereco: EnderecoApi;
  planoSaude: string;
};

export type PacienteListParams = {
  nome?: string;
  cpf?: string;
  ativo?: boolean;
  convenio?: string;
  page?: number;
  size?: number;
  sort?: string;
};

export type PacienteRowViewModel = {
  id: number;
  nome: string;
  cpf: string;
  telefone: string;
  ultimaConsulta: string;
  convenio: string;
  initials: string;
  ativo: boolean;
};

export type PacienteRowsPageViewModel = PageResponse<PacienteRowViewModel>;

export type PacienteFormValues = PacienteApi;

export type PacienteProfileViewModel = {
  id: number;
  nomeCompleto: string;
  initials: string;
  cpf: string;
  idadeLabel: string;
  dataNascimentoLabel: string;
  telefone: string;
  email: string;
  sexo: string;
  planoSaude: string;
  ativo: boolean;
  enderecoCompacto: string;
  enderecoCompleto: string;
  historico: {
    totalConsultas: number;
    totalExamesSolicitados: number;
    totalMedicamentosPrescritos: number;
    dataUltimaConsultaLabel: string;
    ultimaConsulta: {
      id: number;
      dataLabel: string;
      horaLabel: string;
      medicoNome: string;
      statusLabel: string;
      tipoLabel: string;
      motivo: string;
    } | null;
  };
};
