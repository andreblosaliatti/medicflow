// src/views/pages/Consultas/ConsultaDetails.tsx
import { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import PrimaryButton from "../../../../components/ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";

import {
  toConsultaDetailsModel,
  type ConsultaDetailsModel,
  iniciarAtendimento,
  finalizarAtendimento,
} from "../../../../mocks/mappers";

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

  const consulta: ConsultaDetailsModel | null = useMemo(() => {
    if (!id) return null;
    return toConsultaDetailsModel(id);
  }, [id]);

  if (!consulta) {
    return (
      <div className="consultas-page">
        <PageHeader title="Consulta" subtitle="Não encontrada" />
      </div>
    );
  }

  const status = consulta.status as StatusConsulta;

  // PageHeader aceita só 1 action -> aqui fica a ação principal contextual.
  const headerActionLabel = canStart(status)
    ? "Iniciar atendimento"
    : canFinish(status)
      ? "Finalizar atendimento"
      : "Editar";

  const onHeaderAction = () => {
    if (canStart(status)) {
      iniciarAtendimento(consulta.id);
      navigate(`/consultas/${consulta.id}/atendimento`);
      return;
    }
    if (canFinish(status)) {
      finalizarAtendimento(consulta.id);
      navigate(`/consultas/${consulta.id}`);
      return;
    }
    navigate(`/consultas/${consulta.id}/editar`);
  };

  return (
    <div className="consultas-page">
      <PageHeader
        title={`Consulta • ${consulta.pacienteNome}`}
        subtitle={consulta.dataHoraLabel}
        actionLabel={headerActionLabel}
        onAction={onHeaderAction}
      />

      <div className="consultas-detailsGrid">
        <Card>
          <div className="consultas-detailHeader">
            <span className={`mf-badge mf-badge--${consulta.statusTone}`}>
              {consulta.statusLabel}
            </span>
          </div>

          <div className="consultas-kv">
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
              <span className="consultas-k">Contato</span>
              <span className="consultas-v">{consulta.telefoneContato ?? "—"}</span>
            </div>
          </div>

          <div className="consultas-block">
            <div className="consultas-blockTitle">Motivo</div>
            <div className="consultas-blockBody">{consulta.motivo || "—"}</div>
          </div>

          <div className="consultas-actionsBottom">
            {canStart(status) && (
              <PrimaryButton
                onClick={() => {
                  iniciarAtendimento(consulta.id);
                  navigate(`/consultas/${consulta.id}/atendimento`);
                }}
              >
                Iniciar atendimento
              </PrimaryButton>
            )}

            {canFinish(status) && (
              <PrimaryButton
                onClick={() => {
                  finalizarAtendimento(consulta.id);
                  navigate(`/consultas/${consulta.id}`);
                }}
              >
                Finalizar atendimento
              </PrimaryButton>
            )}

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
            <PrimaryButton onClick={() => navigate("/consultas")}>
              Voltar
            </PrimaryButton>
          </div>
        </Card>
      </div>
    </div>
  );
}