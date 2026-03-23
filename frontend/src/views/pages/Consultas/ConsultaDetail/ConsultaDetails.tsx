import { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  useCancelConsultaMutation,
  useConsultaDetailsQuery,
  useConfirmConsultaMutation,
  useFinishConsultaMutation,
  useStartConsultaMutation,
} from "../../../../api/consultas/hooks";
import type { ConsultaDetailsViewModel } from "../../../../api/consultas/types";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import PrimaryButton from "../../../../components/ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import {
  canCancelConsulta,
  canConfirmConsulta,
  canEditConsulta,
  canFinishConsulta,
  canStartConsulta,
} from "../../../../domain/consulta/workflow";

import "./styles.css";
import "../base.css";

export default function ConsultaDetails() {
  const navigate = useNavigate();
  const { id } = useParams();
  const consultaId = useMemo(() => {
    const parsed = Number(id);
    return Number.isFinite(parsed) ? parsed : null;
  }, [id]);

  const { data: consulta, isLoading, error, refetch } = useConsultaDetailsQuery(consultaId);
  const confirmMutation = useConfirmConsultaMutation();
  const cancelMutation = useCancelConsultaMutation();
  const startMutation = useStartConsultaMutation();
  const finishMutation = useFinishConsultaMutation();

  async function handleAction(action: "confirm" | "cancel" | "start" | "finish", current: ConsultaDetailsViewModel) {
    const id = Number(current.id);
    const result = action === "confirm"
      ? await confirmMutation.mutateAsync(id)
      : action === "cancel"
        ? await cancelMutation.mutateAsync(id)
        : action === "start"
          ? await startMutation.mutateAsync(id)
          : await finishMutation.mutateAsync(id);

    if (!result) return;

    if (action === "start") {
      navigate(`/consultas/${current.id}/atendimento`, { replace: true });
      return;
    }

    if (action === "finish") {
      await refetch();
      navigate(`/consultas/${current.id}`, { replace: true });
      return;
    }

    await refetch();
  }

  if (consultaId === null) {
    return (
      <div className="consultas-page">
        <PageHeader title="Consulta" subtitle="Identificador inválido" />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="consultas-page">
        <PageHeader title="Consulta" subtitle="Carregando detalhes" />
      </div>
    );
  }

  if (!consulta) {
    return (
      <div className="consultas-page">
        <PageHeader title="Consulta" subtitle={error ?? "Não encontrada"} />
      </div>
    );
  }

  const canConfirm = canConfirmConsulta(consulta.status);
  const canCancel = canCancelConsulta(consulta.status);
  const canStart = canStartConsulta(consulta.status);
  const canFinish = canFinishConsulta(consulta.status);
  const canEdit = canEditConsulta(consulta.status);
  const mutationError = confirmMutation.error ?? cancelMutation.error ?? startMutation.error ?? finishMutation.error;
  const isMutating = confirmMutation.isPending || cancelMutation.isPending || startMutation.isPending || finishMutation.isPending;

  return (
    <>
      <PageHeader
        title={`Consulta • ${consulta.pacienteNome}`}
        actions={[
          ...(canConfirm ? [{ label: "Confirmar", variant: "secondary" as const, onClick: () => void handleAction("confirm", consulta), disabled: isMutating }] : []),
          ...(canStart ? [{ label: "Iniciar atendimento", variant: "primary" as const, onClick: () => void handleAction("start", consulta), disabled: isMutating }] : []),
          ...(canFinish ? [{ label: "Voltar para atendimento", variant: "secondary" as const, onClick: () => navigate(`/consultas/${consulta.id}/atendimento`) }, { label: "Finalizar atendimento", variant: "primary" as const, onClick: () => void handleAction("finish", consulta), disabled: isMutating }] : []),
          ...(canEdit ? [{ label: "Editar", variant: "secondary" as const, onClick: () => navigate(`/consultas/${consulta.id}/editar`) }] : []),
        ]}
      />

      <div className="mf-page-content">
        <div className="consultas-detailsGrid">
          <Card>
            <div className="consultas-detailHeader">
              <span className={`mf-badge mf-badge--${consulta.statusTone}`}>
                {consulta.statusLabel}
              </span>
            </div>

            {mutationError ? <div className="mf-muted">{mutationError}</div> : null}
            {error ? <div className="mf-muted">{error}</div> : null}

            <div className="consultas-kv">
              <div className="consultas-kvItem">
                <span className="consultas-k">Data/Hora</span>
                <span className="consultas-v">{consulta.dataHoraLabel}</span>
              </div>

              <div className="consultas-kvItem">
                <span className="consultas-k">Paciente</span>
                <span className="consultas-v">{consulta.pacienteNome}</span>
              </div>

              <div className="consultas-kvItem">
                <span className="consultas-k">Médico</span>
                <span className="consultas-v">{consulta.medicoNome}</span>
              </div>

              <div className="consultas-kvItem">
                <span className="consultas-k">Tipo</span>
                <span className="consultas-v">{consulta.tipo}</span>
              </div>

              <div className="consultas-kvItem">
                <span className="consultas-k">Duração</span>
                <span className="consultas-v">{consulta.duracaoMinutos} min</span>
              </div>

              <div className="consultas-kvItem">
                <span className="consultas-k">Sala</span>
                <span className="consultas-v">{consulta.sala ?? "—"}</span>
              </div>

              <div className="consultas-kvItem">
                <span className="consultas-k">Link</span>
                <span className="consultas-v">{consulta.linkAcesso ?? "—"}</span>
              </div>
            </div>

            <div className="consultas-block">
              <div className="consultas-blockTitle">Motivo</div>
              <div className="consultas-blockBody">{consulta.motivo || "—"}</div>
            </div>

            <div className="consultas-actionsBottom">
              {canConfirm ? (
                <SecondaryButton onClick={() => void handleAction("confirm", consulta)} disabled={isMutating}>
                  Confirmar consulta
                </SecondaryButton>
              ) : null}

              {canStart ? (
                <PrimaryButton onClick={() => void handleAction("start", consulta)} disabled={isMutating}>
                  Iniciar atendimento
                </PrimaryButton>
              ) : null}

              {canFinish ? (
                <>
                  <SecondaryButton onClick={() => navigate(`/consultas/${consulta.id}/atendimento`)}>
                    Voltar para atendimento
                  </SecondaryButton>
                  <PrimaryButton onClick={() => void handleAction("finish", consulta)} disabled={isMutating}>
                    Finalizar atendimento
                  </PrimaryButton>
                </>
              ) : null}

              {canEdit ? (
                <SecondaryButton onClick={() => navigate(`/consultas/${consulta.id}/editar`)}>
                  Editar
                </SecondaryButton>
              ) : null}

              {canCancel ? (
                <SecondaryButton onClick={() => void handleAction("cancel", consulta)} disabled={isMutating}>
                  Cancelar consulta
                </SecondaryButton>
              ) : null}

              <SecondaryButton onClick={() => navigate("/consultas")}>
                Voltar
              </SecondaryButton>
            </div>
          </Card>

          <Card>
            <div className="consultas-block">
              <div className="consultas-blockTitle">Financeiro</div>

              <div className="consultas-kv">
                <div className="consultas-kvItem">
                  <span className="consultas-k">Valor</span>
                  <span className="consultas-v">{consulta.valorConsultaLabel}</span>
                </div>

                <div className="consultas-kvItem">
                  <span className="consultas-k">Pago</span>
                  <span className="consultas-v">{consulta.pagoLabel}</span>
                </div>

                <div className="consultas-kvItem">
                  <span className="consultas-k">Meio</span>
                  <span className="consultas-v">{consulta.meioPagamentoLabel}</span>
                </div>
              </div>
            </div>

            <div className="consultas-actionsBottom">
              <PrimaryButton onClick={() => navigate("/consultas")}>Voltar</PrimaryButton>
            </div>
          </Card>
        </div>
      </div>
    </>
  );
}
