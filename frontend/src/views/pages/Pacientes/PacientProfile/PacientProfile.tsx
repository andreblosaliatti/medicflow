import { useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import AppPage from "./../../../../components/layout/AppPage/AppPage";
import PageHeader from "./../../../../components/layout/PageHeader/PageHeader";
import PatientHeaderCard from "../../../../components/pacients/PacientHeader/PacientHeader";
import PatientTabs, { type PatientTab } from "./PacientTabs/PacientTab";
import OverviewTab from "./OverviewTab/OverviewTabs";
import HistoryTab from "./HistoryTab/HistoryTabs";
import { useConsultasByPacienteQuery } from "../../../../api/consultas/hooks";
import { usePacienteProfileQuery } from "../../../../api/pacientes/hooks";

import "./styles.css";

const allowedTabs: readonly PatientTab[] = ["overview", "history", "prontuario", "files", "timeline"];

function resolveTab(input: string | null): PatientTab {
  if (input && allowedTabs.includes(input as PatientTab)) {
    return input as PatientTab;
  }

  return "overview";
}

export default function PacientePerfil() {
  const navigate = useNavigate();
  const params = useParams();
  const [searchParams] = useSearchParams();

  const pacienteId = useMemo(() => {
    const raw = params.id;
    if (!raw) return null;
    const parsed = Number(raw);
    return Number.isFinite(parsed) ? parsed : null;
  }, [params.id]);

  const [activeTab, setActiveTab] = useState<PatientTab>(() => resolveTab(searchParams.get("tab")));
  const pacienteQuery = usePacienteProfileQuery(pacienteId);
  const consultasQuery = useConsultasByPacienteQuery(pacienteId);

  function renderTab() {
    if (!pacienteQuery.data) return null;

    switch (activeTab) {
      case "overview":
        return <OverviewTab patient={pacienteQuery.data} />;
      case "history":
        return (
          <HistoryTab
            items={consultasQuery.data}
            onVerTudo={() => navigate("/consultas")}
          />
        );
      default:
        return <OverviewTab patient={pacienteQuery.data} />;
    }
  }

  const loading = pacienteQuery.isLoading && !pacienteQuery.data;
  const error = pacienteQuery.error ?? consultasQuery.error;

  return (
    <AppPage
      header={<PageHeader title="Perfil do Paciente" />}
      contentClassName="patient-profile-page"
    >
      <div className="mf-page-content">
        {loading ? <div className="mf-muted">Carregando perfil do paciente...</div> : null}
        {error ? <div className="mf-muted">{error}</div> : null}

        {pacienteQuery.data ? (
          <>
            <PatientHeaderCard patient={pacienteQuery.data} />
            <PatientTabs activeTab={activeTab} onChange={setActiveTab} />
            <div className="patient-tab-content">{renderTab()}</div>
          </>
        ) : null}
      </div>
    </AppPage>
  );
}
