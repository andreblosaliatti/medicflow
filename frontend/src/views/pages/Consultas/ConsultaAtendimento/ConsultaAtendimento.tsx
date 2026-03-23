import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  useConsultaDetailsQuery,
  useFinishConsultaMutation,
  useStartConsultaMutation,
  useUpdateConsultaMutation,
} from "../../../../api/consultas/hooks";
import type { ConsultaDetailsViewModel, ConsultaUpdatePayload } from "../../../../api/consultas/types";
import {
  useCreateExameSolicitadoMutation,
  useDeleteExameSolicitadoMutation,
  useExameBaseSearchQuery,
  useExamesByConsultaQuery,
} from "../../../../api/exames/hooks";
import {
  useCreateMedicamentoPrescritoMutation,
  useDeleteMedicamentoPrescritoMutation,
  useMedicamentoBaseSearchQuery,
  useMedicamentosByConsultaQuery,
} from "../../../../api/medicamentos/hooks";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import PrimaryButton from "../../../../components/ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import {
  canFinishConsulta,
  canStartConsulta,
  isTerminalConsulta,
} from "../../../../domain/consulta/workflow";

import "./styles.css";
import "../base.css";

type Tab = "ANAMNESE" | "EXAME_FISICO" | "DIAGNOSTICO" | "MEDICAMENTOS" | "EXAMES" | "OBS";

type AtendimentoForm = {
  anamnese: string;
  exameFisico: string;
  diagnostico: string;
  observacoes: string;
};

type MedicamentoDraft = {
  medicamentoBaseId: number | null;
  nomeLivre: string;
  dosagem: string;
  frequencia: string;
  via: string;
};

type ExameDraft = {
  exameBaseId: number | null;
  justificativa: string;
  observacoes: string;
};

const tabOptions: readonly { key: Tab; label: string }[] = [
  { key: "ANAMNESE", label: "Anamnese" },
  { key: "EXAME_FISICO", label: "Exame físico" },
  { key: "DIAGNOSTICO", label: "Diagnóstico" },
  { key: "MEDICAMENTOS", label: "Medicamentos" },
  { key: "EXAMES", label: "Exames" },
  { key: "OBS", label: "Observações" },
] as const;

const viaOptions: readonly SelectOption<string>[] = [
  { value: "VO", label: "VO" },
  { value: "SL", label: "SL" },
  { value: "IM", label: "IM" },
  { value: "IV", label: "IV" },
  { value: "SC", label: "SC" },
  { value: "TOP", label: "Tópico" },
  { value: "INAL", label: "Inalatória" },
] as const;

function toForm(details: ConsultaDetailsViewModel): AtendimentoForm {
  return {
    anamnese: details.anamnese ?? "",
    exameFisico: details.exameFisico ?? "",
    diagnostico: details.diagnostico ?? "",
    observacoes: details.observacoes ?? "",
  };
}

type AtendimentoEditorProps = {
  consulta: ConsultaDetailsViewModel;
};

function AtendimentoEditor({ consulta }: AtendimentoEditorProps) {
  const navigate = useNavigate();
  const [tab, setTab] = useState<Tab>("ANAMNESE");
  const [form, setForm] = useState<AtendimentoForm>(() => toForm(consulta));
  const [currentStatus, setCurrentStatus] = useState(consulta.status);
  const [medQuery, setMedQuery] = useState("");
  const [medDraft, setMedDraft] = useState<MedicamentoDraft>({
    medicamentoBaseId: null,
    nomeLivre: "",
    dosagem: "",
    frequencia: "",
    via: "VO",
  });
  const [exameQuery, setExameQuery] = useState("");
  const [exameDraft, setExameDraft] = useState<ExameDraft>({
    exameBaseId: null,
    justificativa: "",
    observacoes: "",
  });

  const updateMutation = useUpdateConsultaMutation();
  const startMutation = useStartConsultaMutation();
  const finishMutation = useFinishConsultaMutation();
  const medicamentosQuery = useMedicamentosByConsultaQuery(Number(consulta.id));
  const medicamentoBaseQuery = useMedicamentoBaseSearchQuery(medQuery);
  const createMedicamentoMutation = useCreateMedicamentoPrescritoMutation();
  const deleteMedicamentoMutation = useDeleteMedicamentoPrescritoMutation();
  const examesQuery = useExamesByConsultaQuery(Number(consulta.id));
  const exameBaseQuery = useExameBaseSearchQuery(exameQuery);
  const createExameMutation = useCreateExameSolicitadoMutation();
  const deleteExameMutation = useDeleteExameSolicitadoMutation();

  const medicamentoOptions = useMemo<readonly SelectOption<number>[]>(() => {
    return medicamentoBaseQuery.data.map((item) => ({ value: item.id, label: item.label }));
  }, [medicamentoBaseQuery.data]);

  const exameOptions = useMemo<readonly SelectOption<number>[]>(() => {
    return exameBaseQuery.data.map((item) => ({ value: item.id, label: item.label }));
  }, [exameBaseQuery.data]);

  async function saveProgress() {
    const payload: ConsultaUpdatePayload = {
      anamnese: form.anamnese.trim() || null,
      exameFisico: form.exameFisico.trim() || null,
      diagnostico: form.diagnostico.trim() || null,
      observacoes: form.observacoes.trim() || null,
    };

    return updateMutation.mutateAsync({ id: Number(consulta.id), payload });
  }

  async function ensureStarted() {
    if (currentStatus === "EM_ATENDIMENTO") return true;
    if (!canStartConsulta(currentStatus)) return false;

    const started = await startMutation.mutateAsync(Number(consulta.id));
    if (started) {
      setCurrentStatus(started.status);
      return true;
    }

    return false;
  }

  async function handleSave() {
    const started = await ensureStarted();
    if (!started) return;
    await saveProgress();
  }

  async function handleFinish() {
    const started = await ensureStarted();
    if (!started) return;

    const saved = await saveProgress();
    if (!saved) return;

    const finished = await finishMutation.mutateAsync(Number(consulta.id));
    if (finished) {
      setCurrentStatus(finished.status);
      navigate(`/consultas/${consulta.id}`, { replace: true });
    }
  }

  async function handleAddMedicamento() {
    const started = await ensureStarted();
    if (!started) return;

    const payload = {
      medicamentoBaseId: medDraft.medicamentoBaseId,
      nome: medDraft.medicamentoBaseId ? null : medDraft.nomeLivre.trim() || null,
      dosagem: medDraft.dosagem.trim(),
      frequencia: medDraft.frequencia.trim(),
      via: medDraft.via,
    };

    const created = await createMedicamentoMutation.mutateAsync({ consultaId: Number(consulta.id), payload });
    if (created) {
      setMedQuery("");
      setMedDraft({ medicamentoBaseId: null, nomeLivre: "", dosagem: "", frequencia: "", via: "VO" });
      await medicamentosQuery.refetch();
    }
  }

  async function handleDeleteMedicamento(id: number) {
    const removed = await deleteMedicamentoMutation.mutateAsync(id);
    if (removed === null) return;
    await medicamentosQuery.refetch();
  }

  async function handleAddExame() {
    const started = await ensureStarted();
    if (!started || !exameDraft.exameBaseId) return;

    const created = await createExameMutation.mutateAsync({
      consultaId: Number(consulta.id),
      exameBaseId: exameDraft.exameBaseId,
      status: "SOLICITADO",
      justificativa: exameDraft.justificativa.trim() || undefined,
      observacoes: exameDraft.observacoes.trim() || undefined,
    });

    if (created) {
      setExameQuery("");
      setExameDraft({ exameBaseId: null, justificativa: "", observacoes: "" });
      await examesQuery.refetch();
    }
  }

  async function handleDeleteExame(id: number) {
    const removed = await deleteExameMutation.mutateAsync(id);
    if (removed === null) return;
    await examesQuery.refetch();
  }

  const canStart = canStartConsulta(currentStatus);
  const canFinish = canFinishConsulta(currentStatus) || canStart;
  const isBusy = updateMutation.isPending || startMutation.isPending || finishMutation.isPending;
  const medicationBusy = createMedicamentoMutation.isPending || deleteMedicamentoMutation.isPending;
  const examBusy = createExameMutation.isPending || deleteExameMutation.isPending;
  const mutationError =
    updateMutation.error ??
    startMutation.error ??
    finishMutation.error ??
    createMedicamentoMutation.error ??
    deleteMedicamentoMutation.error ??
    createExameMutation.error ??
    deleteExameMutation.error ??
    medicamentosQuery.error ??
    examesQuery.error ??
    medicamentoBaseQuery.error ??
    exameBaseQuery.error;

  return (
    <div className="consultas-page">
      <PageHeader
        title={`Atendimento • ${consulta.pacienteNome}`}
        subtitle={`Status atual: ${currentStatus === "EM_ATENDIMENTO" ? "Em atendimento" : consulta.statusLabel}`}
        rightSlot={
          <div className="atd-meta">
            <span className="atd-pill">{consulta.dataHoraLabel}</span>
            <span className="atd-pill">{consulta.tipo}</span>
            <span className="atd-pill">{currentStatus === "EM_ATENDIMENTO" ? "Em atendimento" : consulta.statusLabel}</span>
          </div>
        }
        actions={[
          { label: isBusy ? "Salvando..." : "Salvar evolução", variant: "highlight", onClick: () => void handleSave(), disabled: isBusy },
          ...(canFinish ? [{ label: "Finalizar atendimento", variant: "primary" as const, onClick: () => void handleFinish(), disabled: isBusy }] : []),
        ]}
      />

      <Card>
        {mutationError ? <div className="mf-muted">{mutationError}</div> : null}

        <div className="atd-tabs">
          {tabOptions.map((option) => (
            <button
              key={option.key}
              type="button"
              className={`atd-tab ${tab === option.key ? "is-active" : ""}`}
              onClick={() => setTab(option.key)}
            >
              {option.label}
            </button>
          ))}
        </div>

        <div className="atd-panel">
          {tab === "ANAMNESE" ? (
            <label className="atd-field">
              <span className="atd-label">Anamnese</span>
              <textarea
                className="atd-textarea"
                value={form.anamnese}
                onChange={(e) => setForm((current) => ({ ...current, anamnese: e.target.value }))}
                placeholder="História clínica, queixa, antecedentes..."
              />
            </label>
          ) : null}

          {tab === "EXAME_FISICO" ? (
            <label className="atd-field">
              <span className="atd-label">Exame físico</span>
              <textarea
                className="atd-textarea"
                value={form.exameFisico}
                onChange={(e) => setForm((current) => ({ ...current, exameFisico: e.target.value }))}
                placeholder="Sinais vitais, inspeção, palpação, ausculta..."
              />
            </label>
          ) : null}

          {tab === "DIAGNOSTICO" ? (
            <label className="atd-field">
              <span className="atd-label">Diagnóstico</span>
              <textarea
                className="atd-textarea"
                value={form.diagnostico}
                onChange={(e) => setForm((current) => ({ ...current, diagnostico: e.target.value }))}
                placeholder="CID, hipótese diagnóstica, diagnóstico final..."
              />
            </label>
          ) : null}

          {tab === "MEDICAMENTOS" ? (
            <>
              <div className="atd-sectionHeader">
                <h3>Medicamentos prescritos</h3>
                <p>Selecione um item de medicamento base ou informe um nome livre para gerar um medicamento prescrito real.</p>
              </div>

              <div className="atd-grid2">
                <label className="atd-field">
                  <span className="atd-label">Buscar medicamento base</span>
                  <Input
                    value={medQuery}
                    onChange={(e) => setMedQuery(e.target.value)}
                    placeholder="Buscar por DCB ou nome comercial..."
                  />
                </label>

                <label className="atd-field">
                  <span className="atd-label">Medicamento encontrado</span>
                  <SelectField<number>
                    value={medDraft.medicamentoBaseId}
                    onChange={(medicamentoBaseId) => {
                      const selected = medicamentoBaseQuery.data.find((item) => item.id === medicamentoBaseId) ?? null;
                      setMedDraft((current) => ({
                        ...current,
                        medicamentoBaseId,
                        dosagem: current.dosagem || selected?.dosagemPadrao || "",
                        via: current.via || selected?.viaAdministracao || "VO",
                      }));
                    }}
                    options={medicamentoOptions}
                    placeholder="Selecionar medicamento base"
                    ariaLabel="Medicamento base"
                    disabled={medicamentoOptions.length === 0}
                  />
                </label>
              </div>

              <div className="atd-grid2">
                <label className="atd-field">
                  <span className="atd-label">Nome livre (opcional)</span>
                  <Input
                    value={medDraft.nomeLivre}
                    onChange={(e) => setMedDraft((current) => ({ ...current, nomeLivre: e.target.value, medicamentoBaseId: null }))}
                    placeholder="Usar quando não houver medicamento base"
                  />
                </label>

                <label className="atd-field">
                  <span className="atd-label">Via</span>
                  <SelectField<string>
                    value={medDraft.via}
                    onChange={(via) => setMedDraft((current) => ({ ...current, via }))}
                    options={viaOptions}
                    ariaLabel="Via de administração"
                  />
                </label>
              </div>

              <div className="atd-grid2">
                <label className="atd-field">
                  <span className="atd-label">Dosagem</span>
                  <Input
                    value={medDraft.dosagem}
                    onChange={(e) => setMedDraft((current) => ({ ...current, dosagem: e.target.value }))}
                    placeholder="500 mg"
                  />
                </label>

                <label className="atd-field">
                  <span className="atd-label">Frequência / posologia</span>
                  <Input
                    value={medDraft.frequencia}
                    onChange={(e) => setMedDraft((current) => ({ ...current, frequencia: e.target.value }))}
                    placeholder="8/8h por 5 dias"
                  />
                </label>
              </div>

              <div className="atd-sectionActions">
                <PrimaryButton
                  onClick={() => void handleAddMedicamento()}
                  disabled={medicationBusy || (!medDraft.medicamentoBaseId && !medDraft.nomeLivre.trim()) || !medDraft.dosagem.trim() || !medDraft.frequencia.trim()}
                >
                  Adicionar medicamento
                </PrimaryButton>
              </div>

              <div className="atd-list">
                {medicamentosQuery.data.length === 0 ? (
                  <div className="atd-empty">Nenhum medicamento prescrito para esta consulta.</div>
                ) : (
                  medicamentosQuery.data.map((medicamento) => (
                    <div key={medicamento.id} className="atd-item">
                      <div className="atd-itemHeader">
                        <strong className="atd-itemTitle">{medicamento.nome}</strong>
                        <button type="button" className="atd-remove" onClick={() => void handleDeleteMedicamento(medicamento.id)}>
                          Remover
                        </button>
                      </div>
                      <div className="atd-itemMeta">{medicamento.dosagem} • {medicamento.frequencia} • {medicamento.via}</div>
                    </div>
                  ))
                )}
              </div>
            </>
          ) : null}

          {tab === "EXAMES" ? (
            <>
              <div className="atd-sectionHeader">
                <h3>Exames solicitados</h3>
                <p>Selecione um exame base real para registrar a solicitação desta consulta.</p>
              </div>

              <div className="atd-grid2">
                <label className="atd-field">
                  <span className="atd-label">Buscar exame base</span>
                  <Input
                    value={exameQuery}
                    onChange={(e) => setExameQuery(e.target.value)}
                    placeholder="Buscar por nome ou TUSS..."
                  />
                </label>

                <label className="atd-field">
                  <span className="atd-label">Exame encontrado</span>
                  <SelectField<number>
                    value={exameDraft.exameBaseId}
                    onChange={(exameBaseId) => setExameDraft((current) => ({ ...current, exameBaseId }))}
                    options={exameOptions}
                    placeholder="Selecionar exame base"
                    ariaLabel="Exame base"
                    disabled={exameOptions.length === 0}
                  />
                </label>
              </div>

              <label className="atd-field">
                <span className="atd-label">Justificativa</span>
                <textarea
                  className="atd-textarea atd-textarea--compact"
                  value={exameDraft.justificativa}
                  onChange={(e) => setExameDraft((current) => ({ ...current, justificativa: e.target.value }))}
                  placeholder="Justificativa clínica para o exame..."
                />
              </label>

              <label className="atd-field">
                <span className="atd-label">Observações do exame</span>
                <textarea
                  className="atd-textarea atd-textarea--compact"
                  value={exameDraft.observacoes}
                  onChange={(e) => setExameDraft((current) => ({ ...current, observacoes: e.target.value }))}
                  placeholder="Orientações ou observações adicionais..."
                />
              </label>

              <div className="atd-sectionActions">
                <PrimaryButton onClick={() => void handleAddExame()} disabled={examBusy || !exameDraft.exameBaseId}>
                  Adicionar exame
                </PrimaryButton>
              </div>

              <div className="atd-list">
                {examesQuery.data.length === 0 ? (
                  <div className="atd-empty">Nenhum exame solicitado para esta consulta.</div>
                ) : (
                  examesQuery.data.map((exame) => (
                    <div key={exame.id} className="atd-item">
                      <div className="atd-itemHeader">
                        <strong className="atd-itemTitle">{exame.nome}</strong>
                        <button type="button" className="atd-remove" onClick={() => void handleDeleteExame(exame.id)}>
                          Remover
                        </button>
                      </div>
                      <div className="atd-itemMeta">Status: {exame.status}</div>
                      {exame.justificativa ? <div className="atd-itemMeta">Justificativa: {exame.justificativa}</div> : null}
                    </div>
                  ))
                )}
              </div>
            </>
          ) : null}

          {tab === "OBS" ? (
            <label className="atd-field">
              <span className="atd-label">Observações gerais</span>
              <textarea
                className="atd-textarea"
                value={form.observacoes}
                onChange={(e) => setForm((current) => ({ ...current, observacoes: e.target.value }))}
                placeholder="Condutas, orientações e plano de retorno..."
              />
            </label>
          ) : null}

          <div className="atd-actionsBottom">
            <SecondaryButton onClick={() => navigate(`/consultas/${consulta.id}`)}>Voltar para detalhes</SecondaryButton>
            <PrimaryButton onClick={() => void handleSave()} disabled={isBusy}>Salvar evolução</PrimaryButton>
            {canFinish ? (
              <PrimaryButton onClick={() => void handleFinish()} disabled={isBusy}>
                Finalizar atendimento
              </PrimaryButton>
            ) : null}
          </div>
        </div>
      </Card>
    </div>
  );
}

export default function ConsultaAtendimento() {
  const navigate = useNavigate();
  const { id } = useParams();
  const consultaId = useMemo(() => {
    const parsed = Number(id);
    return Number.isFinite(parsed) ? parsed : null;
  }, [id]);
  const { data: consulta, isLoading, error } = useConsultaDetailsQuery(consultaId);

  if (consultaId === null) {
    return (
      <div className="consultas-page">
        <PageHeader title="Atendimento" subtitle="Consulta inválida" />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="consultas-page">
        <PageHeader title="Atendimento" subtitle="Carregando consulta..." />
      </div>
    );
  }

  if (!consulta) {
    return (
      <div className="consultas-page">
        <PageHeader title="Atendimento" subtitle={error ?? "Consulta não encontrada"} />
        <Card>
          <div style={{ padding: 14 }}>
            <PrimaryButton onClick={() => navigate("/consultas")}>Voltar</PrimaryButton>
          </div>
        </Card>
      </div>
    );
  }

  if (isTerminalConsulta(consulta.status)) {
    return (
      <div className="consultas-page">
        <PageHeader
          title={`Atendimento • ${consulta.pacienteNome}`}
          subtitle="Esta consulta já foi encerrada e não pode mais ser atendida."
        />
        <Card>
          <div style={{ padding: 14, display: "flex", gap: 12 }}>
            <SecondaryButton onClick={() => navigate(`/consultas/${consulta.id}`)}>Ver detalhes</SecondaryButton>
            <PrimaryButton onClick={() => navigate("/consultas")}>Voltar para lista</PrimaryButton>
          </div>
        </Card>
      </div>
    );
  }

  return <AtendimentoEditor key={consulta.id} consulta={consulta} />;
}
