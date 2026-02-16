import type { SelectOption } from "../../../components/form/SelectField/SelectField";

export type OrdenarPor = "NOME" | "ULTIMA";
export type FiltroConvenio = "TODOS" | "COM" | "SEM";
export type Exibindo = "TODOS";

export const ordenarOptions: readonly SelectOption<OrdenarPor>[] = [
  { value: "NOME", label: "Nome" },
  { value: "ULTIMA", label: "Última consulta" },
] as const;

export const filtroOptions: readonly SelectOption<FiltroConvenio>[] = [
  { value: "TODOS", label: "Todos" },
  { value: "COM", label: "Com convênio" },
  { value: "SEM", label: "Sem convênio" },
] as const;

export const exibindoOptions: readonly SelectOption<Exibindo>[] = [
  { value: "TODOS", label: "Todos" },
] as const;