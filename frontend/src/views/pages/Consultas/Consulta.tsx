import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import RowMenu from "../../../components/ui/RowMenu/RowMenu";

import { toConsultasRows, type ConsultaRowModel } from "../../../mocks/mappers";

import "./styles.css";

export default function ConsultasPage() {
  const navigate = useNavigate();
  const [menuId, setMenuId] = useState<string | null>(null);

  const rows: ConsultaRowModel[] = useMemo(() => toConsultasRows(), []);

  return (
    <AppPage
      header={
        <PageHeader
        title="Consultas"
        subtitle="Listagem geral"
        actionLabel="Nova consulta"
        onAction={() => navigate("/consultas/nova")}
        />
      }
    >
      <Card>
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
              {rows.map((c) => (
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
                      items={[
                        { key: "details", label: "Ver detalhes" },
                        { key: "edit", label: "Editar", tone: "primary" },
                        { key: "cancel", label: "Cancelar", tone: "danger" },
                      ]}
                      onSelect={(key) => {
                        if (key === "details") navigate(`/consultas/${c.id}`);
                        if (key === "edit") navigate(`/consultas/${c.id}/editar`);
                        if (key === "cancel") console.log("Cancelar (mock):", c.id);
                      }}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </AppPage>
  );
}