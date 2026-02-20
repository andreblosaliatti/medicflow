// src/views/pages/Consultas/ConsultasPage.tsx
import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import RowMenu from "../../../../components/ui/RowMenu/RowMenu";
import Panel from "../../../../components/ui/Panel/Panel";

import {
  toConsultasRows,
  type ConsultaRowModel,
  iniciarAtendimento,
  finalizarAtendimento,
} from "../../../../mocks/mappers";

import type { StatusConsulta } from "../../../../domain/enums/statusConsulta";

import "./styles.css";
import "../base.css";

type MenuKey =
  | "details"
  | "edit"
  | "start"
  | "finish"
  | "cancel";

function canStart(status: StatusConsulta) {
  return status === "AGENDADA" || status === "CONFIRMADA";
}

function canFinish(status: StatusConsulta) {
  return status === "EM_ATENDIMENTO";
}

function canCancel(status: StatusConsulta) {
  return status !== "CONCLUIDA" && status !== "CANCELADA";
}

export default function ConsultasPage() {
  const navigate = useNavigate();
  const [menuId, setMenuId] = useState<string | null>(null);

  const rows: ConsultaRowModel[] = useMemo(() => toConsultasRows(), []);

  return (
    <div className="consultas-page">
      <PageHeader
              title="Consultas"
              actions={[
          {
            label: "Nova Consulta",
            icon: "➕",
            variant: "primary",
            onClick: () => navigate("/consultas/nova"),
          },
        ]}
            />

      <Panel title="Lista de Consultas">
        <div className="consultas-tableWrap">
          <table className="consultas-table">
            <thead>
              <tr>
                <th>Data/Hora</th>
                <th>Paciente</th>
                <th>Tipo</th>
                <th>Duração</th>
                <th>Status</th>
                <th className="consultas-colActions"></th>
              </tr>
            </thead>

            <tbody>
              {rows.map((c) => {
                // ✅ precisa existir no model (status raw)
                const status = c.status as StatusConsulta;

                const items = [
                  { key: "details", label: "Ver detalhes" },
                  { key: "edit", label: "Editar", tone: "primary" as const },

                  ...(canStart(status)
                    ? [{ key: "start", label: "Iniciar atendimento", tone: "primary" as const }]
                    : []),

                  ...(canFinish(status)
                    ? [{ key: "finish", label: "Finalizar atendimento", tone: "primary" as const }]
                    : []),

                  ...(canCancel(status)
                    ? [{ key: "cancel", label: "Cancelar", tone: "danger" as const }]
                    : []),
                ];

                return (
                  <tr
                    key={c.id}
                    onClick={() => navigate(`/consultas/${c.id}`)}
                    className="consultas-row"
                  >
                    <td>{c.dataHoraLabel}</td>
                    <td className="consultas-paciente">{c.pacienteNome}</td>
                    <td>{c.tipo}</td>
                    <td>{c.duracaoMinutos} min</td>
                    <td>
                      <span className={`mf-badge mf-badge--${c.statusTone}`}>
                        {c.statusLabel}
                      </span>
                    </td>

                    <td
                      className="consultas-actions"
                      onClick={(e) => e.stopPropagation()}
                    >
                      <button
                        type="button"
                        className="consultas-more"
                        aria-label="Ações"
                        onClick={() => setMenuId(menuId === c.id ? null : c.id)}
                      >
                        ⋯
                      </button>

                      <RowMenu
                        open={menuId === c.id}
                        onClose={() => setMenuId(null)}
                        items={items}
                        onSelect={(key) => {
                          const k = key as MenuKey;

                          if (k === "details") navigate(`/consultas/${c.id}`);
                          if (k === "edit") navigate(`/consultas/${c.id}/editar`);

                          if (k === "start") {
                            iniciarAtendimento(c.id);
                            navigate(`/consultas/${c.id}/atendimento`);
                          }

                          if (k === "finish") {
                            finalizarAtendimento(c.id);
                            navigate(`/consultas/${c.id}`);
                          }

                          if (k === "cancel") {
                            // mock por enquanto (ideal: cancelarConsulta(c.id))
                            console.log("Cancelar (mock):", c.id);
                            navigate(`/consultas/${c.id}`);
                          }

                          setMenuId(null);
                        }}
                      />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Panel>
    </div>
  );
}