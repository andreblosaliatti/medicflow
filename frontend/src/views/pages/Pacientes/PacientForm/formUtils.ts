import type { PacienteDTO } from "../../../../mocks/db/seed";

export type FormErrorKey =
  | "primeiroNome"
  | "sobrenome"
  | "cpf"
  | "dataNascimento"
  | "telefone"
  | "email"
  | "endereco.cep"
  | "endereco.uf";

export type FormErrors = Partial<Record<FormErrorKey, string>>;

function onlyDigits(value: string) {
  return (value ?? "").replace(/\D/g, "");
}

export function formatCPF(value: string) {
  const digits = onlyDigits(value).slice(0, 11);
  const firstPart = digits.slice(0, 3);
  const secondPart = digits.slice(3, 6);
  const thirdPart = digits.slice(6, 9);
  const suffix = digits.slice(9, 11);

  if (digits.length <= 3) return firstPart;
  if (digits.length <= 6) return `${firstPart}.${secondPart}`;
  if (digits.length <= 9) return `${firstPart}.${secondPart}.${thirdPart}`;

  return `${firstPart}.${secondPart}.${thirdPart}-${suffix}`;
}

export function formatPhoneBR(value: string) {
  const digits = onlyDigits(value).slice(0, 11);
  const ddd = digits.slice(0, 2);
  const rest = digits.slice(2);

  if (digits.length === 0) return "";
  if (digits.length < 3) return `(${ddd}`;
  if (rest.length <= 4) return `(${ddd}) ${rest}`;
  if (rest.length <= 8) return `(${ddd}) ${rest.slice(0, 4)}-${rest.slice(4)}`;

  return `(${ddd}) ${rest.slice(0, 5)}-${rest.slice(5)}`;
}

export function validatePaciente(dto: PacienteDTO): FormErrors {
  const errors: FormErrors = {};

  if (!dto.primeiroNome.trim()) errors.primeiroNome = "Informe o primeiro nome.";
  if (!dto.sobrenome.trim()) errors.sobrenome = "Informe o sobrenome.";

  const cpfDigits = onlyDigits(dto.cpf);
  if (cpfDigits.length !== 11) errors.cpf = "CPF precisa ter 11 dígitos.";

  if (!dto.dataNascimento) errors.dataNascimento = "Informe a data de nascimento.";

  const telDigits = onlyDigits(dto.telefone);
  if (telDigits.length < 10) errors.telefone = "Telefone inválido.";

  if (dto.email && !dto.email.includes("@")) errors.email = "E-mail inválido.";

  const cepDigits = onlyDigits(dto.endereco.cep);
  if (dto.endereco.cep && cepDigits.length !== 8) errors["endereco.cep"] = "CEP deve ter 8 dígitos.";

  if (dto.endereco.uf && dto.endereco.uf.length !== 2) errors["endereco.uf"] = "UF deve ter 2 letras.";

  return errors;
}
