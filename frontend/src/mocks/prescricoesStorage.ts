export type StatusExame = "SOLICITADO" | "AGENDADO" | "REALIZADO" | "CANCELADO";

export type MedicamentoPrescritoMock = {
  id: number;               // Long
  nome: string;
  dosagem: string;
  frequencia: string;
  via: string;

  pacienteId: number;       // ManyToOne Paciente
  consultaId: string;       // ManyToOne Consulta (no teu front é string)
  medicamentoBaseId?: number | null;
};

export type ExameSolicitadoMock = {
  id: number;               // Long
  status: StatusExame;
  justificativa: string;
  observacoes: string;
  dataColeta: string | null;     // LocalDateTime
  dataResultado: string | null;  // LocalDateTime

  consultaId: string;       // ManyToOne Consulta
  pacienteId: number;       // derivado (no backend viria pela Consulta/Paciente)
  exameBaseId?: number | null;
  nome: string;             // vem do ExameBase; no mock simplificamos
};

const KEY = "mf_prescricoes_v1";

type Store = {
  medicamentos: MedicamentoPrescritoMock[];
  exames: ExameSolicitadoMock[];
  seq: number;
};

function readStore(): Store {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) return { medicamentos: [], exames: [], seq: 1000 };
    const parsed = JSON.parse(raw) as Store;
    return {
      medicamentos: Array.isArray(parsed.medicamentos) ? parsed.medicamentos : [],
      exames: Array.isArray(parsed.exames) ? parsed.exames : [],
      seq: typeof parsed.seq === "number" ? parsed.seq : 1000,
    };
  } catch {
    return { medicamentos: [], exames: [], seq: 1000 };
  }
}

function writeStore(next: Store) {
  localStorage.setItem(KEY, JSON.stringify(next));
}

function nextId(store: Store): number {
  const n = store.seq + 1;
  store.seq = n;
  return n;
}

export function seedPrescricoesIfEmpty() {
  const s = readStore();
  if (s.medicamentos.length || s.exames.length) return;

  // Mock simples com pacienteId 1 e 2 e consultas "c1", "c2" etc.
  s.medicamentos = [
    {
      id: 1,
      nome: "Amoxicilina",
      dosagem: "500mg",
      frequencia: "1 comp 8/8h",
      via: "Oral",
      pacienteId: 1,
      consultaId: "c1",
      medicamentoBaseId: 10,
    },
    {
      id: 2,
      nome: "Dipirona",
      dosagem: "1g",
      frequencia: "1 comp se dor",
      via: "Oral",
      pacienteId: 1,
      consultaId: "c2",
      medicamentoBaseId: 11,
    },
    {
      id: 3,
      nome: "Losartana",
      dosagem: "50mg",
      frequencia: "1 comp 1x/dia",
      via: "Oral",
      pacienteId: 2,
      consultaId: "c3",
      medicamentoBaseId: 12,
    },
  ];

  s.exames = [
    {
      id: 10,
      nome: "Hemograma completo",
      status: "SOLICITADO",
      justificativa: "Avaliar anemia/inflamação",
      observacoes: "",
      dataColeta: null,
      dataResultado: null,
      consultaId: "c2",
      pacienteId: 1,
      exameBaseId: 200,
    },
    {
      id: 11,
      nome: "Glicemia jejum",
      status: "AGENDADO",
      justificativa: "Rastreamento metabólico",
      observacoes: "Jejum de 8h",
      dataColeta: null,
      dataResultado: null,
      consultaId: "c3",
      pacienteId: 2,
      exameBaseId: 201,
    },
  ];

  s.seq = 1000;
  writeStore(s);
}

export function getMedicamentosByPacienteId(pacienteId: number): MedicamentoPrescritoMock[] {
  const s = readStore();
  return s.medicamentos.filter((m) => m.pacienteId === pacienteId);
}

export function getExamesByPacienteId(pacienteId: number): ExameSolicitadoMock[] {
  const s = readStore();
  return s.exames.filter((x) => x.pacienteId === pacienteId);
}

export function duplicateMedicamento(id: number): MedicamentoPrescritoMock | null {
  const s = readStore();
  const m = s.medicamentos.find((x) => x.id === id);
  if (!m) return null;

  const copy: MedicamentoPrescritoMock = {
    ...m,
    id: nextId(s),
    // mantém consultaId de origem (mock). Quando integrar de verdade,
    // “repetir” deve criar dentro da consulta atual/rascunho.
  };

  s.medicamentos = [copy, ...s.medicamentos];
  writeStore(s);
  return copy;
}

export function updateExame(
  id: number,
  patch: (prev: ExameSolicitadoMock) => Partial<ExameSolicitadoMock>
): ExameSolicitadoMock | null {
  const s = readStore();
  const idx = s.exames.findIndex((x) => x.id === id);
  if (idx < 0) return null;

  const prev = s.exames[idx];
  const next = { ...prev, ...patch(prev) };

  s.exames[idx] = next;
  writeStore(s);
  return next;
}