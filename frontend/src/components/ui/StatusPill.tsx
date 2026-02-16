import "../../styles/components/status-pill.css";

type Props = {
  status: string; // vem do mapper já formatado
};

function variant(status: string): string {
  const s = status.trim().toLowerCase();

  if (s === "agendada") return "warning";
  if (s === "confirmada") return "success";
  if (s === "em atendimento") return "primary";
  if (s === "concluída" || s === "concluida") return "default";
  if (s === "cancelada") return "danger";

  return "default"; // fallback seguro
}

export default function StatusPill({ status }: Props) {
  const v = variant(status);

  return (
    <span className={`mf-status mf-status--${v}`}>
      <span className="mf-status__dot" aria-hidden="true" />
      <span className="mf-status__text">{status}</span>
    </span>
  );
}