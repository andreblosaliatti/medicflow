import { useMemo } from "react";
import { useNavigate } from "react-router-dom";

import { useConfirmConsultaMutation, useTodayConsultasQuery } from "../../../api/consultas/hooks";
import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import StatCard from "../../../components/ui/StatCard";
import Card from "../../../components/ui/Card";
import ConsultaItem from "../../../components/consulta/ConsultaItem";

import "./styles.css";

function toLocalDateTimeParam(date: Date) {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mi = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}:${ss}`;
}

function startOfToday() {
  const date = new Date();
  date.setHours(0, 0, 0, 0);
  return toLocalDateTimeParam(date);
}

function endOfToday() {
  const date = new Date();
  date.setHours(23, 59, 59, 999);
  return toLocalDateTimeParam(date);
}

export default function DoctorDashboard() {
  const navigate = useNavigate();
  const consultasQuery = useTodayConsultasQuery({
    dataHoraInicio: startOfToday(),
    dataHoraFim: endOfToday(),
    size: 100,
    sort: "dataHora,asc",
  });
  const confirmMutation = useConfirmConsultaMutation();

  const consultasHoje = consultasQuery.data;
  const totalHoje = consultasHoje.length;

  const proxima = useMemo(() => {
    if (!consultasHoje.length) return null;
    return consultasHoje[0];
  }, [consultasHoje]);

  async function handleConfirm(id: string) {
    const updated = await confirmMutation.mutateAsync(Number(id));
    if (updated) {
      void consultasQuery.refetch();
    }
  }

  return (
    <AppPage
      className="doctor-dashboard"
      header={
        <PageHeader
          title="Dashboard"
          actions={[
            {
              label: "Nova Consulta",
              variant: "primary",
              icon: "➕",
              onClick: () => navigate("/consultas/nova"),
            },
            {
              label: "Agenda",
              variant: "primary",
              icon: "🗓️",
              onClick: () => navigate("/agenda"),
            },
          ]}
        />
      }
      contentClassName="mf-page-content"
    >
      <Card className="dash-card">
        <div className="panel-head">
          <div className="panel-title">
            <span className="panel-icon" aria-hidden="true">
              📋
            </span>
            <span>Resumo</span>
          </div>
        </div>

        <div className="dash-stats">
          <StatCard title="Consultas Hoje" value={totalHoje} tone="blue" icon="✅" />

          <StatCard
            title="Próxima Consulta"
            value={proxima ? proxima.hora : "—"}
            subtitle={proxima ? proxima.paciente : "Sem consultas hoje"}
            tone="green"
            icon="🕒"
          />

          <StatCard
            title="Pacientes Novos"
            value={0}
            subtitle="na semana"
            tone="amber"
            icon="👥"
          />

          <StatCard
            title="Pendências"
            value={0}
            subtitle="exames para revisar"
            tone="gray"
            icon="⚠️"
          />
        </div>
      </Card>

      <Card className="dash-card dash-card--flush">
        <div className="panel-head panel-head--between">
          <div className="panel-title">
            <span className="panel-icon" aria-hidden="true">
              🕒
            </span>
            <span>Agenda Hoje</span>
          </div>

          <button className="link-btn" type="button" onClick={() => navigate("/agenda")}>
            Ver agenda completa
          </button>
        </div>

        {(consultasQuery.error || confirmMutation.error) ? (
          <div className="mf-muted">{consultasQuery.error ?? confirmMutation.error}</div>
        ) : null}

        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th style={{ width: 90 }}>Hora</th>
                <th>Paciente</th>
                <th>Profissional</th>
                <th style={{ width: 160 }}>Status</th>
                <th style={{ width: 160 }}>Ações</th>
              </tr>
            </thead>

            <tbody>
              {!consultasQuery.isLoading && consultasHoje.length === 0 ? (
                <tr>
                  <td colSpan={5} className="table-empty">
                    Nenhuma consulta agendada para hoje.
                  </td>
                </tr>
              ) : (
                consultasHoje.map((consulta) => (
                  <ConsultaItem
                    key={consulta.id}
                    data={consulta}
                    onView={(currentId) => navigate(`/consultas/${currentId}`)}
                    onConfirm={(currentId) => void handleConfirm(currentId)}
                    onMore={(currentId) => navigate(`/consultas/${currentId}`)}
                  />
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      <div className="dash-bottom">
        <Card className="dash-card">
          <div className="panel-head">
            <div className="panel-title">
              <span className="panel-icon" aria-hidden="true">
                ⚡
              </span>
              <span>Acesso Rápido</span>
            </div>
          </div>

          <div className="quick-grid">
            <button className="quick-tile" type="button" onClick={() => navigate("/pacientes")}>
              👥 <span>Pacientes</span>
            </button>

            <button className="quick-tile" type="button" onClick={() => navigate("/agenda")}>
              🗓️ <span>Agenda</span>
            </button>

            <button className="quick-tile" type="button" onClick={() => navigate("/consultas")}>
              🩺 <span>Consultas</span>
            </button>

            <button className="quick-tile" type="button" onClick={() => navigate("/pendencias")}>
              ⚠️ <span>Pendências</span>
            </button>
          </div>
        </Card>

        <Card className="dash-card">
          <div className="panel-head">
            <div className="panel-title">
              <span className="panel-icon" aria-hidden="true">
                📌
              </span>
              <span>Hoje</span>
            </div>
          </div>

          <div className="quick-grid">
            <button className="quick-tile quick-tile--disabled" type="button" disabled>
              🧪 <span>Exames</span>
            </button>

            <button className="quick-tile quick-tile--disabled" type="button" disabled>
              📝 <span>Laudos</span>
            </button>

            <button className="quick-tile quick-tile--disabled" type="button" disabled>
              💬 <span>Mensagens</span>
            </button>

            <button className="quick-tile quick-tile--disabled" type="button" disabled>
              📁 <span>Documentos</span>
            </button>
          </div>
        </Card>
      </div>
    </AppPage>
  );
}
