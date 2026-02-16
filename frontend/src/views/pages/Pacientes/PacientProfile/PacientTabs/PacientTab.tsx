import "./styles.css";

export type PatientTab =
  | "overview"
  | "history"
  | "prontuario"
  | "files"
  | "timeline";

type Props = {
  activeTab: PatientTab;
  onChange: (tab: PatientTab) => void;
};

export default function PatientTabs({
  activeTab,
  onChange,
}: Props) {
  return (
    <div className="patient-tabs">
      <button
        className={activeTab === "overview" ? "active" : ""}
        onClick={() => onChange("overview")}
      >
        Visão Geral
      </button>

      <button
        className={activeTab === "history" ? "active" : ""}
        onClick={() => onChange("history")}
      >
        Histórico de Consultas
      </button>

      <button onClick={() => onChange("files")}>
        Arquivos
      </button>

      <button onClick={() => onChange("timeline")}>
        Linha do Tempo
      </button>
    </div>
  );
}