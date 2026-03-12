export type RegistroMedicacaoDTO = {
  id: string; // mock id
  medicamentoBaseId?: string;
  nome: string;
  dosagem?: string;
  frequencia?: string;
  via?: string;
};

export type RegistroExameDTO = {
  id: string; // mock id
  exameBaseId?: string;
  nome: string;
  status: "SOLICITADO" | "REALIZADO" | "RESULTADO";
  justificativa?: string;
  observacoes?: string;
};

export type RegistroClinicoDTO = {
  consultaId: string;

  anamnese?: string;
  exameFisico?: string;
  diagnostico?: string;

  // mantém texto livre (compatível com o que já existe)
  prescricaoTexto?: string;

  // NOVO: estruturado
  medicacoes?: RegistroMedicacaoDTO[];
  exames?: RegistroExameDTO[];

  observacoes?: string;
  updatedAt: string; // ISO
};

export const seedRegistrosClinicos: RegistroClinicoDTO[] = [
  {
    consultaId: "c-1902",
    anamnese:
      "Refere dor lombar com piora ao permanecer sentado. Nega febre. Sem irradiação para membros inferiores.",
    exameFisico:
      "Dor à palpação paravertebral lombar. Lasègue negativo. Força preservada.",
    diagnostico: "Dor lombar baixa (M54.5) + contratura muscular lombar",
    prescricaoTexto:
      "Dipirona 500mg 1cp 8/8h se dor por 5 dias.\nCiclobenzaprina 5mg 1cp à noite por 7 dias (pode causar sonolência).",
    medicacoes: [
      {
        id: "m-1",
        nome: "Dipirona",
        dosagem: "500 mg",
        frequencia: "8/8h se dor",
        via: "VO",
      },
      {
        id: "m-2",
        nome: "Ciclobenzaprina",
        dosagem: "5 mg",
        frequencia: "à noite",
        via: "VO",
      },
    ],
    exames: [
      { id: "e-1", nome: "Raio-X coluna lombar", status: "SOLICITADO" },
      { id: "e-2", nome: "Hemograma completo", status: "SOLICITADO" },
    ],
    observacoes: "Orientado alongamento e retorno em 7 dias.",
    updatedAt: "2026-02-12T15:10:00.000Z",
  },
];