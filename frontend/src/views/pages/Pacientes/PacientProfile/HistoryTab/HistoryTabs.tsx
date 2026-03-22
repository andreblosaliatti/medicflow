import { useMemo, useState } from "react";

import Card from "../../../../../components/ui/Card";
import SelectField, { type SelectOption } from "../../../../../components/form/SelectField/SelectField";
import type { ConsultaHistoryRowViewModel } from "../../../../../api/consultas/types";

import "./styles.css";

type ConsultaStatus = "AGENDADA" | "CONFIRMADA" | "EM_ATENDIMENTO" | "CONCLUIDA" | "CANCELADA";

type RowAction = "open" | "edit" | "cancel";

const rowActions: readonly SelectOption<RowAction>[] = [
  { label: "Abrir", value: "open" },
  { label: "Editar", value: "edit" },
  { label: "Cancelar", value: "cancel" },
] as const;

function statusLabel(s: ConsultaStatus) {
  if (s === "CONFIRMADA") return "Confirmada";
  if (s === "EM_ATENDIMENTO") return "Em atendimento";
  if (s === "CONCLUIDA") return "Concluída";
  if (s === "CANCELADA") return "Cancelada";
  return "Agendada";
}

function statusClass(s: ConsultaStatus) {
  if (s === "CONFIRMADA") return "is-success";
  if (s === "CONCLUIDA") return "is-success";
  if (s === "CANCELADA") return "is-danger";
  if (s === "EM_ATENDIMENTO") return "is-info";
  return "is-muted";
}

type Props = {
  onVerTudo?: () => void;
  onAbrir?: (id: string) => void;
  onEditar?: (id: string) => void;
  onCancelar?: (id: string) => void;
  items?: ConsultaHistoryRowViewModel[];
};

export default function HistoryTab({
  onVerTudo,
  onAbrir,
  onEditar,
  onCancelar,
  items,
}: Props) {
  const data = useMemo(() => items ?? [], [items]);

  const [page, setPage] = useState(1);
  const pageSize = 6;

  const totalPages = Math.max(1, Math.ceil(data.length / pageSize));
  const safePage = Math.min(page, totalPages);

  const paged = useMemo(() => {
    const start = (safePage - 1) * pageSize;
    return data.slice(start, start + pageSize);
  }, [data, safePage]);

  function handleRowAction(action: RowAction, id: string) {
    if (action === "open") onAbrir?.(id);
    if (action === "edit") onEditar?.(id);
    if (action === "cancel") onCancelar?.(id);
  }

  return (
    <Card>
      <div className="history-card__header">
        <h3 className="history-card__title">Histórico de Consultas</h3>

        <button type="button" className="history-card__link" onClick={() => onVerTudo?.()}>
          Ver tudo <span aria-hidden="true">›</span>
        </button>
      </div>

      <div className="history-table-wrap">
        <table className="history-table">
          <thead>
            <tr>
              <th>Data</th>
              <th>Hora</th>
              <th>Profissional</th>
              <th>Status</th>
              <th className="col-actions">Ações</th>
            </tr>
          </thead>

          <tbody>
            {paged.length === 0 ? (
              <tr>
                <td colSpan={5} className="history-empty">
                  Nenhuma consulta encontrada.
                </td>
              </tr>
            ) : (
              paged.map((row) => (
                <tr key={row.id}>
                  <td>{row.data}</td>
                  <td>{row.hora}</td>
                  <td>{row.profissional}</td>
                  <td>
                    <span className={`status-pill ${statusClass(row.status)}`}>
                      {statusLabel(row.status)}
                    </span>
                  </td>
                  <td className="col-actions">
                    <SelectField<RowAction>
                      value={null}
                      onChange={(v) => handleRowAction(v, row.id)}
                      options={rowActions}
                      placeholder="Abrir"
                      ariaLabel={`Ações da consulta ${row.id}`}
                      className="row-actions"
                    />
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="history-footer">
        <div className="history-footer__info">
          {data.length === 0
            ? "0"
            : `${(safePage - 1) * pageSize + 1}–${Math.min(safePage * pageSize, data.length)} de ${data.length}`}
        </div>

        <div className="history-footer__pager">
          <button
            type="button"
            className="pager-btn"
            onClick={() => setPage((current) => Math.max(1, current - 1))}
            disabled={safePage === 1}
          >
            ‹
          </button>

          <span className="pager-page">
            {safePage} / {totalPages}
          </span>

          <button
            type="button"
            className="pager-btn"
            onClick={() => setPage((current) => Math.min(totalPages, current + 1))}
            disabled={safePage === totalPages}
          >
            ›
          </button>
        </div>
      </div>
    </Card>
  );
}
