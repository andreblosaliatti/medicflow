import type { PacienteDTO } from "../../../../mocks/db/seed";

// ✅ adapter: usa a mesma fonte do resto do app
import { getPacientes, savePacientes } from "../../../../mocks/db/storage";

export function getAllPacientes(): PacienteDTO[] {
  return getPacientes() as PacienteDTO[];
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

  const next =
    idx >= 0 ? list.map((p) => (p.id === dto.id ? dto : p)) : [...list, dto];

  savePacientes(next);
  return dto;
}