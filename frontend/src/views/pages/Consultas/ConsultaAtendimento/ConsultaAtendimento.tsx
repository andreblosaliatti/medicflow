import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  useConsultaDetailsQuery,
  useFinishConsultaMutation,
  useStartConsultaMutation,
  useUpdateConsultaMutation,
} from "../../../../api/consultas/hooks";
import type { ConsultaDetailsViewModel, ConsultaUpdatePayload } from "../../../../api/consultas/types";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import PrimaryButton from "../../../../components/ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import {
  canFinishConsulta,
  canStartConsulta,
  isTerminalConsulta,
} from "../../../../domain/consulta/workflow";

import "./styles.css";
import "../base.css";

type Tab = "ANAMNESE" | "EXAME_FISICO" | "DIAGNOSTICO" | "PRESCRICAO" | "OBS";

type AtendimentoForm = {
  anamnese: string;
  exameFisico: string;
  diagnostico: string;
  prescricao: string;
  observacoes: string;
};

const tabOptions: readonly { key: Tab; label: string }[] = [
  { key: "ANAMNESE", label: "Anamnese" },
  { key: "EXAME_FISICO", label: "Exame físico" },
  { key: "DIAGNOSTICO", label: "Diagnóstico" },
  { key: "PRESCRICAO", label: "Prescrição" },
  { key: "OBS", label: "Observações" },
] as const;

function toForm(details: ConsultaDetailsViewModel): AtendimentoForm {
  return {
    anamnese: details.anamnese ?? "",
    exameFisico: details.exameFisico ?? "",
    diagnostico: details.diagnostico ?? "",
    prescricao: details.prescricao ?? "",
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
  const updateMutation = useUpdateConsultaMutation();
  const startMutation = useStartConsultaMutation();
  const finishMutation = useFinishConsultaMutation();

  async function saveProgress() {
    const payload: ConsultaUpdatePayload = {
      anamnese: form.anamnese.trim() || null,
      exameFisico: form.exameFisico.trim() || null,
      diagnostico: form.diagnostico.trim() || null,
      prescricao: form.prescricao.trim() || null,
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

  const canStart = canStartConsulta(currentStatus);
  const canFinish = canFinishConsulta(currentStatus) || canStart;
  const isBusy = updateMutation.isPending || startMutation.isPending || finishMutation.isPending;
  const mutationError = updateMutation.error ?? startMutation.error ?? finishMutation.error;

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
          { label: isBusy ? "Salvando..." : "Salvar", variant: "highlight", onClick: () => void handleSave(), disabled: isBusy },
          ...(canFinish ? [{ label: "Finalizar", variant: "primary" as const, onClick: () => void handleFinish(), disabled: isBusy }] : []),
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

          {tab === "PRESCRICAO" ? (
            <label className="atd-field">
              <span className="atd-label">Prescrição</span>
              <textarea
                className="atd-textarea"
                value={form.prescricao}
                onChange={(e) => setForm((current) => ({ ...current, prescricao: e.target.value }))}
                placeholder="Conduta terapêutica e prescrições..."
              />
            </label>
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
            <SecondaryButton onClick={() => navigate(`/consultas/${consulta.id}`)}>Voltar</SecondaryButton>
            <PrimaryButton onClick={() => void handleSave()} disabled={isBusy}>Salvar</PrimaryButton>
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
