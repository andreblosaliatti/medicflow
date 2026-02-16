import IconButton from "../ui/IconButton";
import StatusPill from "../ui/StatusPill";

export type ConsultaItemModel = {
  id: string;
  hora: string;
  paciente: string;
  profissional: string;
  status: string; // ✅ label de UI (vem do mapper)
};

type Props = {
  data: ConsultaItemModel;
  onView?: (id: string) => void;
  onConfirm?: (id: string) => void;
  onMore?: (id: string) => void;
};

function canConfirm(statusLabel: string) {
  const s = statusLabel.trim().toLowerCase();
  // confirma apenas se ainda não está finalizada/cancelada/confirmada
  if (s === "confirmada" || s === "confirmado") return false;
  if (s === "concluída" || s === "concluida") return false;
  if (s === "cancelada" || s === "cancelado") return false;
  if (s === "em atendimento") return false;
  return true;
}

export default function ConsultaItem({ data, onView, onConfirm, onMore }: Props) {
  const confirmEnabled = canConfirm(data.status);

  return (
    <tr>
      <td className="mf-td mf-mono">{data.hora}</td>

      <td className="mf-td">
        <div className="mf-person">
          <span className="mf-avatar" aria-hidden="true">
            👤
          </span>
          <span className="mf-person__name">{data.paciente}</span>
        </div>
      </td>

      <td className="mf-td mf-muted">{data.profissional}</td>

      <td className="mf-td">
        <StatusPill status={data.status} />
      </td>

      <td className="mf-td">
        <div className="mf-row-actions">
          <IconButton icon="👁️" title="Ver" onClick={() => onView?.(data.id)} />

          <IconButton
            icon="✅"
            title={confirmEnabled ? "Confirmar" : "Já confirmado"}
            onClick={() => confirmEnabled && onConfirm?.(data.id)}
            disabled={!confirmEnabled}
          />

          <IconButton icon="⋯" title="Mais" onClick={() => onMore?.(data.id)} />
        </div>
      </td>
    </tr>
  );
}