import { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { useConsultaDetailsQuery, useFinishConsultaMutation, useStartConsultaMutation } from "../../../../api/consultas/hooks";
import type { ConsultaDetailsViewModel } from "../../../../api/consultas/types";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import PrimaryButton from "../../../../components/ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import type { StatusConsulta } from "../../../../domain/enums/statusConsulta";

import "./styles.css";
import "../base.css";

function canStart(status: StatusConsulta) {
  return status === "AGENDADA" || status === "CONFIRMADA";
}

function canFinish(status: StatusConsulta) {
  return status === "EM_ATENDIMENTO";
}

export default function ConsultaDetails() {
  const navigate = useNavigate();
  const { id } = useParams();
  const consultaId = useMemo(() => {
    const parsed = Number(id);
    return Number.isFinite(parsed) ? parsed : null;
  }, [id]);

  const { data: consulta, isLoading, error } = useConsultaDetailsQuery(consultaId);
  const startMutation = useStartConsultaMutation();
  const finishMutation = useFinishConsultaMutation();

  async function handlePrimaryAction(current: ConsultaDetailsViewModel) {
    if (canStart(current.status)) {
      const started = await startMutation.mutateAsync(Number(current.id));
      if (started) {
        navigate(`/consultas/${current.id}/atendimento`, { replace: true });
      }
      return;
    }

    if (canFinish(current.status)) {
      const finished = await finishMutation.mutateAsync(Number(current.id));
      if (finished) {
        navigate(`/consultas/${current.id}`, { replace: true });
      }
      return;
    }

    navigate(`/consultas/${current.id}/editar`);
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

  const headerActionLabel = canStart(consulta.status)
    ? "Iniciar atendimento"
    : canFinish(consulta.status)
      ? "Finalizar atendimento"
      : "Editar";

  const workflowError = startMutation.error ?? finishMutation.error;
  const workflowPending = startMutation.isPending || finishMutation.isPending;

  return (
    <>
      <PageHeader
        title={`Consulta • ${consulta.pacienteNome}`}
        actionLabel={headerActionLabel}
        onAction={() => void handlePrimaryAction(consulta)}
      />

      <div className="mf-page-content">
        <div className="consultas-detailsGrid">
          <Card>
            <div className="consultas-detailHeader">
              <span className={`mf-badge mf-badge--${consulta.statusTone}`}>
                {consulta.statusLabel}
              </span>
            </div>

            {workflowError ? <div className="mf-muted">{workflowError}</div> : null}
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
              {canStart(consulta.status) ? (
                <PrimaryButton onClick={() => void handlePrimaryAction(consulta)} disabled={workflowPending}>
                  Iniciar atendimento
                </PrimaryButton>
              ) : null}

              {canFinish(consulta.status) ? (
                <PrimaryButton onClick={() => void handlePrimaryAction(consulta)} disabled={workflowPending}>
                  Finalizar atendimento
                </PrimaryButton>
              ) : null}

              <SecondaryButton onClick={() => navigate(`/consultas/${consulta.id}/editar`)}>
                Editar
              </SecondaryButton>

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
