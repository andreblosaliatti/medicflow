export type Sexo = "MASCULINO" | "FEMININO" | "OUTRO" | "NAO_INFORMAR";

export type Endereco = {
  logradouro: string;
  numero: string;
  complemento: string;
  bairro: string;
  cidade: string;
  uf: string;
  cep: string;
};

export type Paciente = {
  id: number;
  primeiroNome: string;
  sobrenome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  sexo: Sexo;
  endereco: Endereco;
  planoSaude: string;
  ativo: boolean;
};

export function emptyPaciente(nextId: number): Paciente {
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
}