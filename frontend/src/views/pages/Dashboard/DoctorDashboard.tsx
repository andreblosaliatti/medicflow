import { useMemo } from "react";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import StatCard from "../../../components/ui/StatCard";
import Card from "../../../components/ui/Card";
import ConsultaItem from "../../../components/consulta/ConsultaItem";

import { toConsultasHojeItems } from "../../../mocks/mappers";

import "./styles.css";

export default function DoctorDashboard() {
  const consultasHoje = useMemo(() => toConsultasHojeItems(), []);
  const totalHoje = consultasHoje.length;

  const proxima = useMemo(() => {
    if (!consultasHoje.length) return null;
    const sorted = [...consultasHoje].sort((a, b) => a.hora.localeCompare(b.hora));
    return sorted[0];
  }, [consultasHoje]);

  return (
    <AppPage
      className="doctor-dashboard"
      header={
      <PageHeader
        title="Dashboard"
        actions={[
          { label: "Nova Consulta", variant: "primary", icon: "➕" },
          { label: "Agenda", variant: "primary", icon: "🗓️" },
        ]}
      />
      }
      contentClassName="page-content"
    >
        {/* ===== Resumo ===== */}
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

            <StatCard title="Pacientes Novos" value={0} subtitle="na semana" tone="amber" icon="👥" />
            <StatCard title="Pendências" value={0} subtitle="exames para revisar" tone="gray" icon="⚠️" />
          </div>
        </Card>

        {/* ===== Agenda Hoje ===== */}
        <Card className="dash-card dash-card--flush">
          <div className="panel-head panel-head--between">
            <div className="panel-title">
              <span className="panel-icon" aria-hidden="true">
                🕒
              </span>
              <span>Agenda Hoje</span>
            </div>

            <button className="link-btn" type="button" onClick={() => console.log("Ir para agenda completa")}>
              Ver agenda completa
            </button>
          </div>

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
                {consultasHoje.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="table-empty">
                      Nenhuma consulta agendada para hoje.
                    </td>
                  </tr>
                ) : (
                  consultasHoje.map((c) => (
                    <ConsultaItem
                      key={c.id}
                      data={c}
                      onView={(id) => console.log("Ver", id)}
                      onConfirm={(id) => console.log("Confirmar", id)}
                      onMore={(id) => console.log("Mais", id)}
                    />
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>

        {/* ===== Atalhos ===== */}
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
              <button className="quick-tile" type="button">
                👥 <span>Pacientes</span>
              </button>
              <button className="quick-tile" type="button">
                🗓️ <span>Agenda</span>
              </button>
              <button className="quick-tile" type="button">
                🩺 <span>Consultas</span>
              </button>
              <button className="quick-tile" type="button">
                📄 <span>Prescrições</span>
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
              <button className="quick-tile" type="button">
                🧪 <span>Exames</span>
              </button>
              <button className="quick-tile" type="button">
                📝 <span>Laudos</span>
              </button>
              <button className="quick-tile" type="button">
                💬 <span>Mensagens</span>
              </button>
              <button className="quick-tile" type="button">
                📁 <span>Documentos</span>
              </button>
            </div>
          </Card>
        </div>
    </AppPage>
  );
}