import { useMemo } from "react";
import { useNavigate } from "react-router-dom";

import {
  useCancelConsultaMutation,
  useConfirmConsultaMutation,
  useFinishConsultaMutation,
  useOperationalPendingItemsQuery,
  useStartConsultaMutation,
} from "../../../api/consultas/hooks";
import type { OperationalPendingItemViewModel } from "../../../api/consultas/types";
import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../components/ui/Panel/Panel";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../components/ui/Table/Table";

import "./styles.css";

function toLocalDateTimeParam(date: Date) {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mi = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}:${ss}`;
}

function nowToNextDaysRange(days: number) {
  const start = new Date();
  start.setHours(0, 0, 0, 0);

  const end = new Date(start);
  end.setDate(end.getDate() + days);
  end.setHours(23, 59, 59, 999);

  return {
    start: toLocalDateTimeParam(start),
    end: toLocalDateTimeParam(end),
  };
}

export default function PendenciasPage() {
  const navigate = useNavigate();

  const range = nowToNextDaysRange(7);
  const pendingQuery = useOperationalPendingItemsQuery({
    dataHoraInicio: range.start,
    dataHoraFim: range.end,
    sort: "dataHora,asc",
    size: 200,
  });

  const confirmMutation = useConfirmConsultaMutation();
  const cancelMutation = useCancelConsultaMutation();
  const startMutation = useStartConsultaMutation();
  const finishMutation = useFinishConsultaMutation();

  const mutationError = confirmMutation.error ?? cancelMutation.error ?? startMutation.error ?? finishMutation.error;

  const { proximas, prioritarias, outras } = useMemo(() => {
    const rows = pendingQuery.data;

    return {
      proximas: rows.slice(0, 6),
      prioritarias: rows.filter((item) => item.prioridade === "ALTA" || item.status === "AGENDADA").slice(0, 6),
      outras: rows.filter((item) => item.prioridade !== "ALTA" && item.status !== "AGENDADA").slice(0, 8),
    };
  }, [pendingQuery.data]);

  async function handleAction(row: OperationalPendingItemViewModel) {
    const id = Number(row.id);

    if (row.status === "AGENDADA") {
      await confirmMutation.mutateAsync(id);
    } else if (row.status === "CONFIRMADA") {
      const started = await startMutation.mutateAsync(id);
      if (started) {
        navigate(`/consultas/${row.id}/atendimento`);
        return;
      }
    } else if (row.status === "EM_ATENDIMENTO") {
      await finishMutation.mutateAsync(id);
    }

    await pendingQuery.refetch();
  }

  return (
    <AppPage header={<PageHeader title="Pendências" />}>
      <div className="mf-page-content pendencias-page">
        {pendingQuery.error || mutationError ? <div className="mf-muted">{pendingQuery.error ?? mutationError}</div> : null}

        <Panel title="Próximas Consultas" icon="🗓️">
          <PendenciasTable
            rows={proximas}
            onOpen={(id) => navigate(`/consultas/${id}`)}
            onAction={(row) => void handleAction(row)}
          />
        </Panel>

        <Panel title="Pendências Prioritárias" icon="⚠️">
          <PendenciasTable
            rows={prioritarias}
            onOpen={(id) => navigate(`/consultas/${id}`)}
            onAction={(row) => void handleAction(row)}
          />
        </Panel>

        <Panel title="Outras Pendências" icon="📌">
          <PendenciasTable
            rows={outras}
            onOpen={(id) => navigate(`/consultas/${id}`)}
            onAction={(row) => void handleAction(row)}
          />
        </Panel>
      </div>
    </AppPage>
  );
}

function PendenciasTable({
  rows,
  onOpen,
  onAction,
}: {
  rows: OperationalPendingItemViewModel[];
  onOpen: (id: string) => void;
  onAction: (row: OperationalPendingItemViewModel) => void;
}) {
  return (
    <TableWrap>
      <Table>
        <THead>
          <tr>
            <Th>Paciente</Th>
            <Th style={{ width: 170 }}>Pendência</Th>
            <Th style={{ width: 170 }}>Data</Th>
            <Th style={{ width: 120 }}>Prioridade</Th>
            <Th style={{ width: 150 }}>Status</Th>
            <Th style={{ width: 140 }} align="right">
              Ação
            </Th>
          </tr>
        </THead>

        <TBody>
          {rows.length === 0 ? (
            <Tr>
              <Td className="pendencias-empty" colSpan={6}>
                Nenhuma pendência aqui.
              </Td>
            </Tr>
          ) : (
            rows.map((r) => (
              <Tr
                key={r.id}
                onClick={() => onOpen(r.id)}
                ariaLabel={`Abrir pendência de ${r.pacienteNome}`}
              >
                <Td>
                  <div className="mf-person">
                    <div className="mf-avatar" aria-hidden="true">
                      {initials(r.pacienteNome)}
                    </div>
                    <div className="pendencias-person">
                      <div className="mf-person__name">{r.pacienteNome}</div>
                      <div className="pendencias-sub mf-muted">
                        {r.medicoNome}
                      </div>
                    </div>
                  </div>
                </Td>

                <Td className="mf-muted">{r.pendenciaLabel}</Td>

                <Td className="mf-mono">{r.dataHoraLabel}</Td>

                <Td>
                  <span className={`mf-badge pendencias-prio pendencias-prio--${r.prioridadeTone}`}>
                    {r.prioridade}
                  </span>
                </Td>

                <Td>
                  <span className={`mf-badge mf-badge--${r.statusTone}`}>{r.statusLabel}</span>
                </Td>

                <Td align="right" onClick={(e) => e.stopPropagation()}>
                  {(r.status === "AGENDADA" || r.status === "CONFIRMADA" || r.status === "EM_ATENDIMENTO") ? (
                    <button
                      type="button"
                      className="pendencias-action pendencias-action--primary"
                      onClick={() => onAction(r)}
                    >
                      {r.status === "AGENDADA" ? "Confirmar" : r.status === "CONFIRMADA" ? "Iniciar" : "Finalizar"}
                    </button>
                  ) : (
                    <span className="pendencias-noaction mf-muted">—</span>
                  )}
                </Td>
              </Tr>
            ))
          )}
        </TBody>
      </Table>
    </TableWrap>
  );
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/).slice(0, 2);
  return parts.map((p) => p[0]?.toUpperCase()).join("");
}
