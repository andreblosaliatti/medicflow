import { useMemo } from "react";
import { useNavigate } from "react-router-dom";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../components/ui/Panel/Panel";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../components/ui/Table/Table";

import { toConsultasRows, type ConsultaRowModel } from "../../../mocks/mappers";

import "./styles.css";

type Priority = "ALTA" | "MEDIA" | "BAIXA";

type PendenciaRow = ConsultaRowModel & {
  pendenciaLabel: string;
  prioridade: Priority;
  prioridadeTone: "danger" | "warn" | "muted";
};

export default function PendenciasPage() {
  const navigate = useNavigate();

  const { proximas, prioritarias, outras } = useMemo(() => {
    const now = new Date();

    const all = toConsultasRows()
      .slice()
      .sort((a, b) => {
        const da = parseDateFromLabel(a.dataHoraLabel)?.getTime() ?? 0;
        const db = parseDateFromLabel(b.dataHoraLabel)?.getTime() ?? 0;
        return da - db;
      })
      .map((c) => toPendenciaRow(c, now));

    const abertas = all.filter((x) => x.status !== "CANCELADA" && x.status !== "CONCLUIDA");

    const proximas = abertas
      .filter((x) => {
        const dt = parseDateFromLabel(x.dataHoraLabel);
        return dt ? dt.getTime() >= now.getTime() : false;
      })
      .slice(0, 6);

    const prioritarias = abertas
      .filter((x) => {
        const dt = parseDateFromLabel(x.dataHoraLabel);
        if (!dt) return false;

        const diffMs = dt.getTime() - now.getTime();
        const isOverdue = diffMs < 0;
        const within24h = diffMs >= 0 && diffMs <= 24 * 60 * 60 * 1000;

        return isOverdue || (within24h && x.status === "AGENDADA") || x.prioridade === "ALTA";
      })
      .slice(0, 6);

    const used = new Set([...proximas, ...prioritarias].map((x) => x.id));
    const outras = abertas.filter((x) => !used.has(x.id)).slice(0, 8);

    return { proximas, prioritarias, outras };
  }, []);

  return (
    <AppPage header={<PageHeader title="Pendências" />}>
      <div className="mf-page-content pendencias-page">
        <Panel title="Próximas Consultas" icon="🗓️">
          <PendenciasTable
            rows={proximas}
            onOpen={(id) => navigate(`/consultas/${id}`)}
            onConfirm={(id) => console.log("Confirmar (mock):", id)}
          />
        </Panel>

        <Panel title="Pendências Prioritárias" icon="⚠️">
          <PendenciasTable
            rows={prioritarias}
            onOpen={(id) => navigate(`/consultas/${id}`)}
            onConfirm={(id) => console.log("Confirmar (mock):", id)}
          />
        </Panel>

        <Panel title="Outras Pendências" icon="📌">
          <PendenciasTable
            rows={outras}
            onOpen={(id) => navigate(`/consultas/${id}`)}
            onConfirm={(id) => console.log("Confirmar (mock):", id)}
          />
        </Panel>
      </div>
    </AppPage>
  );
}

function PendenciasTable({
  rows,
  onOpen,
  onConfirm,
}: {
  rows: PendenciaRow[];
  onOpen: (id: string) => void;
  onConfirm: (id: string) => void;
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
              Confirmar
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
                        {r.medicoNome} • {r.sala ?? "—"}
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
                  {r.status === "AGENDADA" ? (
                    <button
                      type="button"
                      className="pendencias-action pendencias-action--primary"
                      onClick={() => onConfirm(r.id)}
                    >
                      Confirmar
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

function toPendenciaRow(c: ConsultaRowModel, now: Date): PendenciaRow {
  const dt = parseDateFromLabel(c.dataHoraLabel);
  const diffMs = dt ? dt.getTime() - now.getTime() : 0;

  const isOverdue = dt ? diffMs < 0 : false;
  const within24h = dt ? diffMs >= 0 && diffMs <= 24 * 60 * 60 * 1000 : false;
  const within3d = dt ? diffMs > 24 * 60 * 60 * 1000 && diffMs <= 3 * 24 * 60 * 60 * 1000 : false;

  let prioridade: Priority = "BAIXA";
  if (isOverdue || (within24h && c.status === "AGENDADA")) prioridade = "ALTA";
  else if (within3d) prioridade = "MEDIA";

  const prioridadeTone = prioridade === "ALTA" ? "danger" : prioridade === "MEDIA" ? "warn" : "muted";

  const pendenciaLabel =
    c.status === "AGENDADA"
      ? "Confirmar consulta"
      : c.status === "CONFIRMADA"
        ? "Aguardando atendimento"
        : c.status === "EM_ATENDIMENTO"
          ? "Em atendimento"
          : "Revisar";

  return {
    ...c,
    pendenciaLabel,
    prioridade,
    prioridadeTone,
  };
}

// Converte "dd/mm/yyyy hh:mm" (que vem do teu datetimeLabel) em Date
function parseDateFromLabel(label: string): Date | null {
  const m = label.match(/^(\d{2})\/(\d{2})\/(\d{4})\s+(\d{2}):(\d{2})$/);
  if (!m) return null;

  const dd = Number(m[1]);
  const mm = Number(m[2]);
  const yyyy = Number(m[3]);
  const hh = Number(m[4]);
  const mi = Number(m[5]);

  const dt = new Date(yyyy, mm - 1, dd, hh, mi, 0, 0);
  return Number.isNaN(dt.getTime()) ? null : dt;
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/).slice(0, 2);
  return parts.map((p) => p[0]?.toUpperCase()).join("");
}