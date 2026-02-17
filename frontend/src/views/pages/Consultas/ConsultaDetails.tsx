import { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";

import { toConsultaDetailsModel, type ConsultaDetailsModel } from "../../../mocks/mappers";

import "./styles.css";

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

  return (
    <div className="consultas-page">
      <PageHeader
        title={`Consulta • ${consulta.pacienteNome}`}
        subtitle={consulta.dataHoraLabel}
        actionLabel="Editar"
        onAction={() => navigate(`/consultas/${consulta.id}/editar`)}
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