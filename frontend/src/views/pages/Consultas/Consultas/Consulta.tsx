import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  useCancelConsultaMutation,
  useConfirmConsultaMutation,
  useConsultasQuery,
  useFinishConsultaMutation,
  useStartConsultaMutation,
} from "../../../../api/consultas/hooks";
import type { ConsultaRowViewModel } from "../../../../api/consultas/types";
import AppPage from "../../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import Panel from "../../../../components/ui/Panel/Panel";
import RowMenu from "../../../../components/ui/RowMenu/RowMenu";
import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../../components/ui/Table/Table";
import type { StatusConsulta } from "../../../../domain/enums/statusConsulta";
import {
  canCancelConsulta,
  canConfirmConsulta,
  canEditConsulta,
  canFinishConsulta,
  canStartConsulta,
} from "../../../../domain/consulta/workflow";

import "./styles.css";

type StatusFilter = StatusConsulta | "TODOS";

const statusOptions: readonly SelectOption<StatusFilter>[] = [
  { value: "TODOS", label: "Todos" },
  { value: "AGENDADA", label: "Agendada" },
  { value: "CONFIRMADA", label: "Confirmada" },
  { value: "EM_ATENDIMENTO", label: "Em atendimento" },
  { value: "CONCLUIDA", label: "Concluída" },
  { value: "CANCELADA", label: "Cancelada" },
] as const;

export default function ConsultasPage() {
  const navigate = useNavigate();
  const [menuId, setMenuId] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("TODOS");

  const queryParams = useMemo(
    () => ({ status: statusFilter === "TODOS" ? undefined : statusFilter }),
    [statusFilter],
  );

  const { data: rows, isLoading, error, refetch } = useConsultasQuery(queryParams);
  const confirmMutation = useConfirmConsultaMutation();
  const cancelMutation = useCancelConsultaMutation();
  const startMutation = useStartConsultaMutation();
  const finishMutation = useFinishConsultaMutation();

  const mutationError = confirmMutation.error ?? cancelMutation.error ?? startMutation.error ?? finishMutation.error;
  const isMutating = confirmMutation.isPending || cancelMutation.isPending || startMutation.isPending || finishMutation.isPending;

  async function runAction(action: "confirm" | "cancel" | "start" | "finish" | "resume", row: ConsultaRowViewModel) {
    const id = Number(row.id);

    if (action === "resume") {
      navigate(`/consultas/${row.id}/atendimento`);
      return;
    }

    const result = action === "confirm"
      ? await confirmMutation.mutateAsync(id)
      : action === "cancel"
        ? await cancelMutation.mutateAsync(id)
        : action === "start"
          ? await startMutation.mutateAsync(id)
          : await finishMutation.mutateAsync(id);

    if (!result) return;

    if (action === "start") {
      navigate(`/consultas/${row.id}/atendimento`, { replace: true });
      return;
    }

    if (action === "finish") {
      navigate(`/consultas/${row.id}`);
      return;
    }

    await refetch();
  }

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
        <Panel
          title="Lista de Consultas"
          icon="🗓️"
          right={
            <div style={{ minWidth: 220 }}>
              <SelectField<StatusFilter>
                value={statusFilter}
                onChange={setStatusFilter}
                options={statusOptions}
                ariaLabel="Filtrar por status"
              />
            </div>
          }
        >
          {error ? <div className="mf-muted">{error}</div> : null}
          {mutationError ? <div className="mf-muted">{mutationError}</div> : null}

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

                {rows.map((row) => {
                  const items = [
                    { key: "details", label: "Ver detalhes" },
                    ...(canConfirmConsulta(row.status) ? [{ key: "confirm", label: "Confirmar", tone: "primary" as const }] : []),
                    ...(canStartConsulta(row.status) ? [{ key: "start", label: "Iniciar atendimento", tone: "primary" as const }] : []),
                    ...(canFinishConsulta(row.status) ? [{ key: "resume", label: "Voltar para atendimento", tone: "primary" as const }, { key: "finish", label: "Finalizar atendimento", tone: "primary" as const }] : []),
                    ...(canEditConsulta(row.status) ? [{ key: "edit", label: "Editar", tone: "primary" as const }] : []),
                    ...(canCancelConsulta(row.status) ? [{ key: "cancel", label: "Cancelar", tone: "danger" as const }] : []),
                  ];

                  return (
                    <Tr
                      key={row.id}
                      onClick={() => navigate(`/consultas/${row.id}`)}
                      ariaLabel={`Abrir consulta de ${row.pacienteNome} em ${row.dataHoraLabel}`}
                    >
                      <Td className="mf-mono">{row.dataHoraLabel}</Td>

                      <Td>
                        <div className="mf-person">
                          <div className="mf-avatar" aria-hidden="true">
                            {initials(row.pacienteNome)}
                          </div>
                          <span className="mf-person__name">{row.pacienteNome}</span>
                        </div>
                      </Td>

                      <Td className="mf-muted">{row.tipo}</Td>
                      <Td>{row.duracaoMinutos} min</Td>

                      <Td>
                        <span className={`mf-badge mf-badge--${row.statusTone}`}>
                          {row.statusLabel}
                        </span>
                      </Td>

                      <Td align="right" onClick={(e) => e.stopPropagation()}>
                        <div className="mf-row-actions">
                          <button
                            type="button"
                            className="consultas-more"
                            aria-label="Ações"
                            onClick={() => setMenuId(menuId === row.id ? null : row.id)}
                            disabled={isMutating}
                          >
                            ⋯
                          </button>

                          <RowMenu
                            open={menuId === row.id}
                            onClose={() => setMenuId(null)}
                            items={items}
                            onSelect={(key) => {
                              if (key === "details") navigate(`/consultas/${row.id}`);
                              if (key === "edit") navigate(`/consultas/${row.id}/editar`);
                              if (key === "confirm") void runAction("confirm", row);
                              if (key === "cancel") void runAction("cancel", row);
                              if (key === "start") void runAction("start", row);
                              if (key === "resume") void runAction("resume", row);
                              if (key === "finish") void runAction("finish", row);
                            }}
                          />
                        </div>
                      </Td>
                    </Tr>
                  );
                })}
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
  return parts.map((part) => part[0]?.toUpperCase()).join("");
}
