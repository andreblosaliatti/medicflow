import { useState } from "react";
import AppPage from "./../../../../components/layout/AppPage/AppPage";
import PageHeader from "./../../../../components/layout/PageHeader/PageHeader";
import PatientHeaderCard from "../../../../components/pacients/PacientHeader/PacientHeader";
import PatientTabs from "./PacientTabs/PacientTab";
import OverviewTab from "./OverviewTab/OverviewTabs";
import HistoryTab from "./HistoryTab/HistoryTabs";

import "./styles.css";

type TabType =
  | "overview"
  | "history"
  | "prontuario"
  | "files"
  | "timeline";

export default function PacientePerfil() {
  const [activeTab, setActiveTab] = useState<TabType>("overview");

  function renderTab() {
    switch (activeTab) {
      case "overview":
        return <OverviewTab />;
      case "history":
        return <HistoryTab />;
      default:
        return <OverviewTab />;
    }
  }

  return (
    <AppPage
      header={<PageHeader title="Perfil do Paciente" />}
      contentClassName="patient-profile-page"
    >
        <PatientHeaderCard />

        <PatientTabs
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        <div className="patient-tab-content">
          {renderTab()}
        </div>
    </AppPage>
  );
}