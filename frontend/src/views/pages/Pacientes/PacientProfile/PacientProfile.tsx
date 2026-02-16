import { useState } from "react";
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
    <>
      <PageHeader title="Perfil do Paciente" />

      <div className="patient-profile-page">
        <PatientHeaderCard />

        <PatientTabs
          activeTab={activeTab}
          onChange={setActiveTab}
        />

        <div className="patient-tab-content">
          {renderTab()}
        </div>
      </div>
    </>
  );
}