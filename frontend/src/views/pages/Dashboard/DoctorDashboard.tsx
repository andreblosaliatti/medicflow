import { useMemo } from "react";
import { useNavigate } from "react-router-dom";

import {
  useConfirmConsultaMutation,
  useOperationalPendingItemsQuery,
  useTodayConsultasQuery,
  useUpcomingAppointmentsQuery,
} from "../../../api/consultas/hooks";
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

function dayRange(offsetDays = 0) {
  const start = new Date();
  start.setDate(start.getDate() + offsetDays);
  start.setHours(0, 0, 0, 0);

  const end = new Date(start);
  end.setHours(23, 59, 59, 999);

  return {
    start: toLocalDateTimeParam(start),
    end: toLocalDateTimeParam(end),
  };
}

function nextDaysRange(daysAhead: number) {
  const start = new Date();
  start.setSeconds(0, 0);

  const end = new Date(start);
  end.setDate(end.getDate() + daysAhead);
  end.setHours(23, 59, 59, 999);

  return {
    start: toLocalDateTimeParam(start),
    end: toLocalDateTimeParam(end),
  };
}

export default function DoctorDashboard() {
  const navigate = useNavigate();
  const today = dayRange();
  const upcoming = nextDaysRange(7);

  const consultasQuery = useTodayConsultasQuery({
    dataHoraInicio: today.start,
    dataHoraFim: today.end,
    size: 100,
    sort: "dataHora,asc",
  });

  const upcomingQuery = useUpcomingAppointmentsQuery({
    dataHoraInicio: upcoming.start,
    dataHoraFim: upcoming.end,
    size: 100,
    sort: "dataHora,asc",
  });

  const pendingQuery = useOperationalPendingItemsQuery({
    dataHoraInicio: upcoming.start,
    dataHoraFim: upcoming.end,
    size: 100,
    sort: "dataHora,asc",
  });

  const confirmMutation = useConfirmConsultaMutation();

  const consultasHoje = consultasQuery.data;
  const totalHoje = consultasHoje.length;
  const totalPendencias = pendingQuery.data.length;

  const proximaHoje = useMemo(() => {
    if (!consultasHoje.length) return null;
    return consultasHoje[0];
  }, [consultasHoje]);

  const proximaAgendada = upcomingQuery.data[0] ?? null;

  async function handleConfirm(id: string) {
    const updated = await confirmMutation.mutateAsync(Number(id));
    if (updated) {
      await consultasQuery.refetch();
      await upcomingQuery.refetch();
      await pendingQuery.refetch();
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
            value={proximaHoje ? proximaHoje.hora : "—"}
            subtitle={proximaHoje ? proximaHoje.paciente : "Sem consultas hoje"}
            tone="green"
            icon="🕒"
          />

          <StatCard
            title="Próximos Agendamentos"
            value={upcomingQuery.data.length}
            subtitle={proximaAgendada ? `${proximaAgendada.pacienteNome} • ${proximaAgendada.dataHoraLabel}` : "Sem próximos agendamentos"}
            tone="amber"
            icon="📆"
          />

          <StatCard
            title="Pendências"
            value={totalPendencias}
            subtitle="fluxos operacionais em aberto"
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

        {(consultasQuery.error || confirmMutation.error || upcomingQuery.error || pendingQuery.error) ? (
          <div className="mf-muted">{consultasQuery.error ?? confirmMutation.error ?? upcomingQuery.error ?? pendingQuery.error}</div>
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
            <button className="quick-tile" type="button" onClick={() => navigate("/pendencias")}>
              ⚠️ <span>{totalPendencias} pendências</span>
            </button>

            <button className="quick-tile" type="button" onClick={() => navigate("/consultas")}> 
              📆 <span>{upcomingQuery.data.length} agendamentos</span>
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
