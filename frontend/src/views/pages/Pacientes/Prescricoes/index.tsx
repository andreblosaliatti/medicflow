import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import AppPage from "../../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../../components/ui/Panel/Panel";
import RowMenu from "../../../../components/ui/RowMenu/RowMenu";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../../components/ui/Table/Table";
import { useExamesByPacienteQuery, useUpdateExameMutation } from "../../../../api/exames/hooks";
import type { StatusExameApi } from "../../../../api/exames/types";
import { useMedicamentosByPacienteQuery } from "../../../../api/medicamentos/hooks";
import type { MedicamentoViewModel } from "../../../../api/medicamentos/types";
import { usePacienteProfileQuery } from "../../../../api/pacientes/hooks";

import "./styles.css";

type Tab = "MEDICAMENTOS" | "EXAMES";

type MenuAction =
  | "OPEN_CONSULTA"
  | "IMPRIMIR_MED"
  | "SET_COLETA"
  | "SET_RESULTADO"
  | "CANCELAR_EXAME";

const statusOptions: readonly SelectOption<StatusExameApi>[] = [
  { value: "SOLICITADO", label: "SOLICITADO" },
  { value: "AGENDADO", label: "AGENDADO" },
  { value: "REALIZADO", label: "REALIZADO" },
  { value: "CANCELADO", label: "CANCELADO" },
] as const;

export default function PrescricoesPage() {
  const navigate = useNavigate();
  const params = useParams();
  const pacienteId = Number(params.id);
  const pacienteIdValue = Number.isFinite(pacienteId) ? pacienteId : null;

  const [tab, setTab] = useState<Tab>("MEDICAMENTOS");
  const [menuKey, setMenuKey] = useState<string | null>(null);

  const { data: medicamentos, error: medicamentosError } =
    useMedicamentosByPacienteQuery(pacienteIdValue);
  const { data: exames, refetch: refetchExames, error: examesError } = useExamesByPacienteQuery(pacienteIdValue);
  const { data: pacienteProfile } = usePacienteProfileQuery(pacienteIdValue);
  const { mutateAsync: updateExame } = useUpdateExameMutation();

  const pacienteNome = useMemo(() => {
    if (!Number.isFinite(pacienteId)) return "Paciente";
    return pacienteProfile?.nomeCompleto ?? `Paciente #${pacienteId}`;
  }, [pacienteId, pacienteProfile?.nomeCompleto]);

  function openConsulta(consultaId: number) {
    navigate(`/consultas/${consultaId}`);
  }

  async function onSelectAction(action: MenuAction, payload: { consultaId: number; medId?: number; exameId?: number }) {
    setMenuKey(null);

    if (action === "OPEN_CONSULTA") {
      openConsulta(payload.consultaId);
      return;
    }


    if (action === "IMPRIMIR_MED" && payload.medId != null) {
      const medicamento = medicamentos.find((item) => item.id === payload.medId);
      if (medicamento) printMedicamento(pacienteNome, medicamento);
      return;
    }

    if (action === "SET_COLETA" && payload.exameId != null) {
      const updated = await updateExame({ exameId: payload.exameId, dataColeta: new Date().toISOString() });
      if (updated) {
        await refetchExames();
      }
      return;
    }

    if (action === "SET_RESULTADO" && payload.exameId != null) {
      const updated = await updateExame({ exameId: payload.exameId, dataResultado: new Date().toISOString() });
      if (updated) {
        await refetchExames();
      }
      return;
    }

    if (action === "CANCELAR_EXAME" && payload.exameId != null) {
      const updated = await updateExame({
        exameId: payload.exameId,
        status: "CANCELADO",
        dataColeta: null,
        dataResultado: null,
      });

      if (updated) {
        await refetchExames();
      }
    }
  }

  return (
    <AppPage
      onClick={() => setMenuKey(null)}
      header={<PageHeader title="Prescrições" subtitle={pacienteNome} />}
    >
      <div className="mf-page-content prescricoes-page">
        <div className="prescricoes-tabs" role="tablist" aria-label="Prescrições">
          <button
            type="button"
            className={tab === "MEDICAMENTOS" ? "quick-tile is-active" : "quick-tile"}
            onClick={() => setTab("MEDICAMENTOS")}
            role="tab"
            aria-selected={tab === "MEDICAMENTOS"}
          >
            💊 <span>Medicações</span>
            <span className="prescricoes-count">{medicamentos.length}</span>
          </button>

          <button
            type="button"
            className={tab === "EXAMES" ? "quick-tile is-active" : "quick-tile"}
            onClick={() => setTab("EXAMES")}
            role="tab"
            aria-selected={tab === "EXAMES"}
          >
            🧪 <span>Exames</span>
            <span className="prescricoes-count">{exames.length}</span>
          </button>
        </div>

        {tab === "MEDICAMENTOS" ? (
          <Panel title="Medicações prescritas" icon="💊">
            {medicamentosError ? <div className="mf-muted">{medicamentosError}</div> : null}
            <TableWrap>
              <Table>
                <THead>
                  <tr>
                    <Th>Medicamento</Th>
                    <Th style={{ width: 160 }}>Dosagem</Th>
                    <Th style={{ width: 180 }}>Frequência</Th>
                    <Th style={{ width: 120 }}>Via</Th>
                    <Th style={{ width: 120 }} align="right">
                      Ações
                    </Th>
                  </tr>
                </THead>

                <TBody>
                  {medicamentos.length === 0 ? (
                    <Tr>
                      <Td className="prescricoes-empty" colSpan={5}>
                        Nenhuma medicação registrada.
                      </Td>
                    </Tr>
                  ) : (
                    medicamentos.map((m) => {
                      const key = `MED:${m.id}`;
                      return (
                        <Tr
                          key={m.id}
                          onClick={() => openConsulta(m.consultaId)}
                          ariaLabel={`Abrir consulta da medicação ${m.nome}`}
                        >
                          <Td>
                            <div className="prescricoes-main">
                              <div className="prescricoes-title">{m.nome}</div>
                              <div className="prescricoes-sub mf-muted">Consulta #{m.consultaId}</div>
                            </div>
                          </Td>

                          <Td className="mf-mono">{m.dosagem || "—"}</Td>
                          <Td>{m.frequencia || "—"}</Td>
                          <Td>{m.via || "—"}</Td>

                          <Td align="right" onClick={(e) => e.stopPropagation()}>
                            <div className="mf-row-actions">
                              <button
                                type="button"
                                className="prescricoes-more"
                                aria-label="Ações"
                                onClick={() => setMenuKey(menuKey === key ? null : key)}
                              >
                                ⋯
                              </button>

                              <RowMenu
                                open={menuKey === key}
                                onClose={() => setMenuKey(null)}
                                items={[
                                  { key: "OPEN_CONSULTA", label: "Ver consulta" },
                                  { key: "IMPRIMIR_MED", label: "Imprimir" },
                                ]}
                                onSelect={(selected) =>
                                  void onSelectAction(selected as MenuAction, { consultaId: m.consultaId, medId: m.id })
                                }
                              />
                            </div>
                          </Td>
                        </Tr>
                      );
                    })
                  )}
                </TBody>
              </Table>
            </TableWrap>
          </Panel>
        ) : (
          <Panel title="Exames solicitados" icon="🧪">
            {examesError ? <div className="mf-muted">{examesError}</div> : null}
            <TableWrap>
              <Table>
                <THead>
                  <tr>
                    <Th>Exame</Th>
                    <Th style={{ width: 150 }}>Status</Th>
                    <Th style={{ width: 220 }}>Justificativa</Th>
                    <Th style={{ width: 170 }}>Coleta</Th>
                    <Th style={{ width: 170 }}>Resultado</Th>
                    <Th style={{ width: 120 }} align="right">
                      Ações
                    </Th>
                  </tr>
                </THead>

                <TBody>
                  {exames.length === 0 ? (
                    <Tr>
                      <Td className="prescricoes-empty" colSpan={6}>
                        Nenhum exame registrado.
                      </Td>
                    </Tr>
                  ) : (
                    exames.map((x) => {
                      const key = `EX:${x.id}`;
                      return (
                        <Tr
                          key={x.id}
                          onClick={() => openConsulta(x.consultaId)}
                          ariaLabel={`Abrir consulta do exame ${x.nome}`}
                        >
                          <Td>
                            <div className="prescricoes-main">
                              <div className="prescricoes-title">{x.nome}</div>
                              <div className="prescricoes-sub mf-muted">Consulta #{x.consultaId}</div>
                              {x.justificativa ? (
                                <div className="prescricoes-sub mf-muted">Justificativa: {x.justificativa}</div>
                              ) : null}
                            </div>
                          </Td>

                          <Td onClick={(e) => e.stopPropagation()}>
                            <SelectField<StatusExameApi>
                              value={x.status}
                              onChange={(value) => void updateExameStatus(x.id, value, refetchExames, updateExame)}
                              options={statusOptions}
                              ariaLabel={`Status do exame ${x.nome}`}
                            />
                          </Td>

                          <Td>{x.justificativa || "—"}</Td>
                          <Td className="mf-mono">{formatDateTime(x.dataColeta)}</Td>
                          <Td className="mf-mono">{formatDateTime(x.dataResultado)}</Td>

                          <Td align="right" onClick={(e) => e.stopPropagation()}>
                            <div className="mf-row-actions">
                              <button
                                type="button"
                                className="prescricoes-more"
                                aria-label="Ações"
                                onClick={() => setMenuKey(menuKey === key ? null : key)}
                              >
                                ⋯
                              </button>

                              <RowMenu
                                open={menuKey === key}
                                onClose={() => setMenuKey(null)}
                                items={[
                                  { key: "OPEN_CONSULTA", label: "Ver consulta" },
                                  { key: "SET_COLETA", label: "Registrar coleta" },
                                  { key: "SET_RESULTADO", label: "Registrar resultado" },
                                  { key: "CANCELAR_EXAME", label: "Cancelar", tone: "danger" },
                                ]}
                                onSelect={(selected) =>
                                  void onSelectAction(selected as MenuAction, { consultaId: x.consultaId, exameId: x.id })
                                }
                              />
                            </div>
                          </Td>
                        </Tr>
                      );
                    })
                  )}
                </TBody>
              </Table>
            </TableWrap>
          </Panel>
        )}
      </div>
    </AppPage>
  );
}

async function updateExameStatus(
  exameId: number,
  status: StatusExameApi,
  refetch: () => Promise<void>,
  mutate: ReturnType<typeof useUpdateExameMutation>["mutateAsync"],
) {
  const updated = await mutate({ exameId, status });
  if (updated) {
    await refetch();
  }
}

function formatDateTime(value: string | null) {
  if (!value) return "—";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function printMedicamento(pacienteNome: string, medicamento: MedicamentoViewModel) {
  const popup = window.open("", "_blank", "noopener,noreferrer,width=720,height=640");
  if (!popup) return;

  popup.document.write(`
    <html lang="pt-BR">
      <head>
        <title>Receita - ${escapeHtml(medicamento.nome)}</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 32px; color: #1f2937; }
          h1 { margin: 0 0 8px; }
          .muted { color: #6b7280; margin-bottom: 24px; }
          .box { border: 1px solid #d1d5db; border-radius: 12px; padding: 20px; }
        </style>
      </head>
      <body>
        <h1>Prescrição</h1>
        <div class="muted">Paciente: ${escapeHtml(pacienteNome)} • Consulta #${escapeHtml(String(medicamento.consultaId))}</div>
        <div class="box">
          <p><strong>Medicamento:</strong> ${escapeHtml(medicamento.nome)}</p>
          <p><strong>Dosagem:</strong> ${escapeHtml(medicamento.dosagem || "—")}</p>
          <p><strong>Frequência:</strong> ${escapeHtml(medicamento.frequencia || "—")}</p>
          <p><strong>Via:</strong> ${escapeHtml(medicamento.via || "—")}</p>
        </div>
        <script>window.print()</script>
      </body>
    </html>
  `);
  popup.document.close();
}

function escapeHtml(value: string) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
