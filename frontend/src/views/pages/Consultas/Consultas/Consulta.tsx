import { useState } from "react";
import { useNavigate } from "react-router-dom";

import AppPage from "../../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../../components/ui/Panel/Panel";
import RowMenu from "../../../../components/ui/RowMenu/RowMenu";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../../components/ui/Table/Table";

import { useConsultasQuery } from "../../../../api/consultas/hooks";
import type { ConsultaRowViewModel } from "../../../../api/consultas/types";

import "./styles.css";

export default function ConsultasPage() {
  const navigate = useNavigate();
  const [menuId, setMenuId] = useState<string | null>(null);
  const { data: rows, isLoading, error } = useConsultasQuery();

  return (
    <AppPage
      onClick={() => setMenuId(null)}
      header={
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
      }
    >
      <div className="mf-page-content">
        <Panel title="Lista de Consultas" icon="🗓️">
          {error ? <div className="mf-muted">{error}</div> : null}

          <TableWrap>
            <Table>
              <THead>
                <tr>
                  <Th style={{ width: 180 }}>Data/Hora</Th>
                  <Th>Paciente</Th>
                  <Th style={{ width: 160 }}>Tipo</Th>
                  <Th style={{ width: 120 }}>Duração</Th>
                  <Th style={{ width: 140 }}>Status</Th>
                  <Th style={{ width: 150 }} align="right">
                    Ações
                  </Th>
                </tr>
              </THead>

              <TBody>
                {!isLoading && rows.length === 0 ? (
                  <Tr>
                    <Td colSpan={6} className="mf-muted">
                      Nenhuma consulta encontrada.
                    </Td>
                  </Tr>
                ) : null}

                {rows.map((c: ConsultaRowViewModel) => (
                  <Tr
                    key={c.id}
                    onClick={() => navigate(`/consultas/${c.id}`)}
                    ariaLabel={`Abrir consulta de ${c.pacienteNome} em ${c.dataHoraLabel}`}
                  >
                    <Td className="mf-mono">{c.dataHoraLabel}</Td>

                    <Td>
                      <div className="mf-person">
                        <div className="mf-avatar" aria-hidden="true">
                          {initials(c.pacienteNome)}
                        </div>
                        <span className="mf-person__name">{c.pacienteNome}</span>
                      </div>
                    </Td>

                    <Td className="mf-muted">{c.tipo}</Td>
                    <Td>{c.duracaoMinutos} min</Td>

                    <Td>
                      <span className={`mf-badge mf-badge--${c.statusTone}`}>
                        {c.statusLabel}
                      </span>
                    </Td>

                    <Td align="right" onClick={(e) => e.stopPropagation()}>
                      <div className="mf-row-actions">
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
                      </div>
                    </Td>
                  </Tr>
                ))}
              </TBody>
            </Table>
          </TableWrap>
        </Panel>
      </div>
    </AppPage>
  );
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/).slice(0, 2);
  return parts.map((p) => p[0]?.toUpperCase()).join("");
}
