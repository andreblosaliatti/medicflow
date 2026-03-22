export type PacienteApi = {
  id: number;
  primeiroNome: string;
  sobrenome: string;
  telefone: string;
  planoSaude: string;
};

export type PacienteRowViewModel = {
  id: number;
  nome: string;
  telefone: string;
  ultimaConsulta: string;
  convenio: string;
  initials: string;
};
