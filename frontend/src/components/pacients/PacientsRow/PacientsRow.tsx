import { useLocation, useNavigate } from "react-router-dom";
import RowMenu, { type RowMenuItem } from "../../ui/RowMenu/RowMenu";

export type PacienteModel = {
  id: number;
  nome: string;
  telefone: string;
  ultimaConsulta: string;
  convenio: string;
};

export type PacienteMenuAction =
  | "PRONTUARIO"
  | "VER_PERFIL"
  | "EDITAR"
  | "NOVA_CONSULTA"
  | "ENVIAR_MENSAGEM"
  | "ARQUIVAR";

type Props = {
  paciente: PacienteModel;
  menuOpen: boolean;
  onToggleMenu: (id: number) => void;
  onCloseMenu: () => void;
  onAction: (pacienteId: number, action: PacienteMenuAction) => void;
};

type NavState = { from?: string };

const menuItems: RowMenuItem[] = [
  { key: "VER_PERFIL", label: "Ver perfil", icon: "👤" },
  { key: "EDITAR", label: "Editar", icon: "✏️" },
  { key: "NOVA_CONSULTA", label: "Nova consulta", icon: "🗓️" },
  { key: "ENVIAR_MENSAGEM", label: "Enviar mensagem", icon: "💬" },
  { key: "ARQUIVAR", label: "Arquivar", icon: "🗄️", tone: "danger" },
];

function isMenuAction(key: string): key is PacienteMenuAction {
  return (
    key === "VER_PERFIL" ||
    key === "EDITAR" ||
    key === "NOVA_CONSULTA" ||
    key === "ENVIAR_MENSAGEM" ||
    key === "ARQUIVAR" ||
    key === "PRONTUARIO"
  );
}

function initials(nome: string) {
  const parts = nome.trim().split(/\s+/).slice(0, 2);
  const ini = parts.map((p) => p[0]?.toUpperCase()).join("");
  return ini || "P";
}

export default function PacientsRow({
  paciente,
  menuOpen,
  onToggleMenu,
  onCloseMenu,
  onAction,
}: Props) {
  const navigate = useNavigate();
  const location = useLocation();

  const from = `${location.pathname}${location.search}`;
  const navState: NavState = { from };

  function handleAction(action: PacienteMenuAction) {
    // navegações ficam aqui (é melhor do que “delegar” e duplicar lógica no pai)
    if (action === "EDITAR") {
      navigate(`/pacientes/${paciente.id}/editar`, { state: navState });
      return;
    }

    if (action === "VER_PERFIL") {
      navigate(`/pacientes/${paciente.id}`, { state: navState });
      return;
    }

    // demais ações continuam indo pro handler do pai
    onAction(paciente.id, action);
  }

  return (
    <tr>
      <td className="mf-td">
        <div className="mf-person">
          <span className="mf-avatar" aria-hidden="true">
            {initials(paciente.nome)}
          </span>
          <span className="mf-person__name">{paciente.nome}</span>
        </div>
      </td>

      <td className="mf-td">{paciente.telefone}</td>
      <td className="mf-td mf-muted">{paciente.ultimaConsulta}</td>
      <td className="mf-td">{paciente.convenio}</td>

      <td className="mf-td mf-actions-cell">
        <div className="mf-row-actions">
          <div className="mf-split">
            <div className="mf-split__buttons">
              <button
                type="button"
                className="mf-split__main"
                onClick={(e) => {
                  e.stopPropagation();
                  handleAction("PRONTUARIO");
                }}
                title="Abrir prontuário"
              >
                📄 <span>Prontuário</span>
              </button>

              <button
                type="button"
                className="mf-split__caret"
                onMouseDown={(e) => e.stopPropagation()}
                onClick={(e) => {
                  e.stopPropagation();
                  onToggleMenu(paciente.id);
                }}
                aria-label="Mais ações"
                title="Mais ações"
              >
                ▾
              </button>
            </div>

            <RowMenu
              open={menuOpen}
              onClose={onCloseMenu}
              items={menuItems}
              onSelect={(key) => {
                if (isMenuAction(key)) handleAction(key);
              }}
            />
          </div>
        </div>
      </td>
    </tr>
  );
}