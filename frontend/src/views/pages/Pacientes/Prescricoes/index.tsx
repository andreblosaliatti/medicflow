import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import AppPage from "../../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../../components/ui/Panel/Panel";
import RowMenu from "../../../../components/ui/RowMenu/RowMenu";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../../components/ui/Table/Table";
import { pacienteNomeById } from "../../../../mocks/mappers";

import {
  seedPrescricoesIfEmpty,
  getMedicamentosByPacienteId,
  getExamesByPacienteId,
  duplicateMedicamento,
  updateExame,
  type MedicamentoPrescritoMock,
  type ExameSolicitadoMock,
  type StatusExame,
} from "../../../../mocks/prescricoesStorage";

import "./styles.css";

type Tab = "MEDICAMENTOS" | "EXAMES";

type MenuAction =
  | "OPEN_CONSULTA"
  | "REPETIR_MED"
  | "IMPRIMIR_MED"
  | "SET_COLETA"
  | "SET_RESULTADO"
  | "CANCELAR_EXAME";

const statusOptions: readonly SelectOption<StatusExame>[] = [
  { value: "SOLICITADO", label: "SOLICITADO" },
  { value: "AGENDADO", label: "AGENDADO" },
  { value: "REALIZADO", label: "REALIZADO" },
  { value: "CANCELADO", label: "CANCELADO" },
] as const;

export default function PrescricoesPage() {
  const navigate = useNavigate();
  const params = useParams();
  const pacienteId = Number(params.id);

  const [tab, setTab] = useState<Tab>("MEDICAMENTOS");
  const [menuKey, setMenuKey] = useState<string | null>(null);

  useMemo(() => {
    seedPrescricoesIfEmpty();
  }, []);

  const pacienteNome = useMemo(() => {
    if (!Number.isFinite(pacienteId)) return "Paciente";
    return pacienteNomeById(pacienteId);
  }, [pacienteId]);

  const medicamentos = useMemo(() => {
    if (!Number.isFinite(pacienteId)) return [];
    return getMedicamentosByPacienteId(pacienteId);
  }, [pacienteId]);

  const exames = useMemo(() => {
    if (!Number.isFinite(pacienteId)) return [];
    return getExamesByPacienteId(pacienteId);
  }, [pacienteId]);

  function openConsulta(consultaId: string) {
    navigate(`/consultas/${consultaId}`);
  }

  function refreshUI() {
    // mock: força re-render simples sem mexer em store global
    setTab((t) => (t === "MEDICAMENTOS" ? "EXAMES" : "MEDICAMENTOS"));
    setTab((t) => (t === "MEDICAMENTOS" ? "EXAMES" : "MEDICAMENTOS"));
  }

  function onSelectAction(action: MenuAction, payload: { consultaId: string; medId?: number; exameId?: number }) {
    setMenuKey(null);

    if (action === "OPEN_CONSULTA") {
      openConsulta(payload.consultaId);
      return;
    }

    if (action === "REPETIR_MED" && payload.medId != null) {
      duplicateMedicamento(payload.medId);
      refreshUI();
      return;
    }

    if (action === "IMPRIMIR_MED" && payload.medId != null) {
      const m = medicamentos.find((x) => x.id === payload.medId);
      if (m) printMedicamento(pacienteNome, m);
      return;
    }

    if (action === "SET_COLETA" && payload.exameId != null) {
      updateExame(payload.exameId, () => ({ dataColeta: new Date().toISOString() }));
      refreshUI();
      return;
    }

    if (action === "SET_RESULTADO" && payload.exameId != null) {
      updateExame(payload.exameId, () => ({ dataResultado: new Date().toISOString() }));
      refreshUI();
      return;
    }

    if (action === "CANCELAR_EXAME" && payload.exameId != null) {
      updateExame(payload.exameId, () => ({
        status: "CANCELADO",
        dataColeta: null,
        dataResultado: null,
      }));
      refreshUI();
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
                                  { key: "REPETIR_MED", label: "Repetir", tone: "primary" },
                                  { key: "IMPRIMIR_MED", label: "Imprimir" },
                                ]}
                                onSelect={(k) =>
                                  onSelectAction(k as MenuAction, { consultaId: m.consultaId, medId: m.id })
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
            <TableWrap>
              <Table>
                <THead>
                  <tr>
                    <Th>Exame</Th>
                    <Th style={{ width: 180 }}>Status</Th>
                    <Th style={{ width: 160 }}>Coleta</Th>
                    <Th style={{ width: 160 }}>Resultado</Th>
                    <Th style={{ width: 120 }} align="right">
                      Ações
                    </Th>
                  </tr>
                </THead>

                <TBody>
                  {exames.length === 0 ? (
                    <Tr>
                      <Td className="prescricoes-empty" colSpan={5}>
                        Nenhum exame registrado.
                      </Td>
                    </Tr>
                  ) : (
                    exames.map((x) => {
                      const key = `EXA:${x.id}`;
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
                            <SelectField<StatusExame>
                              value={x.status}
                              onChange={(v) => {
                                updateExame(x.id, (prev) => {
                                  const now = new Date().toISOString();
                                  const next: Partial<ExameSolicitadoMock> = { status: v };

                                  if (v === "REALIZADO") {
                                    next.dataColeta = prev.dataColeta ?? now;
                                  }
                                  if (v === "CANCELADO") {
                                    next.dataColeta = null;
                                    next.dataResultado = null;
                                  }
                                  return next;
                                });
                                refreshUI();
                              }}
                              options={statusOptions}
                              ariaLabel="Status do exame"
                              className="mf-select--embedded"
                            />
                          </Td>

                          <Td className="mf-mono">{formatISOToBR(x.dataColeta) ?? "—"}</Td>
                          <Td className="mf-mono">{formatISOToBR(x.dataResultado) ?? "—"}</Td>

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
                                  { key: "SET_COLETA", label: "Marcar coleta (hoje)" },
                                  { key: "SET_RESULTADO", label: "Marcar resultado (hoje)", tone: "primary" },
                                  ...(x.status !== "CANCELADO"
                                    ? [{ key: "CANCELAR_EXAME", label: "Cancelar", tone: "danger" as const }]
                                    : []),
                                ]}
                                onSelect={(k) =>
                                  onSelectAction(k as MenuAction, { consultaId: x.consultaId, exameId: x.id })
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

function formatISOToBR(iso: string | null): string | null {
  if (!iso) return null;
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return null;

  const two = (n: number) => String(n).padStart(2, "0");
  return `${two(d.getDate())}/${two(d.getMonth() + 1)}/${d.getFullYear()} ${two(d.getHours())}:${two(d.getMinutes())}`;
}

function printMedicamento(pacienteNome: string, m: MedicamentoPrescritoMock) {
  const html = `
  <html>
    <head>
      <title>Receita - ${pacienteNome}</title>
      <meta charset="utf-8" />
      <style>
        body{ font-family: Arial, sans-serif; padding:24px; }
        h1{ font-size:18px; margin:0 0 16px; }
        .box{ border:1px solid #ddd; border-radius:12px; padding:16px; }
        .row{ margin:8px 0; }
        .muted{ color:#555; font-size:12px; }
      </style>
    </head>
    <body>
      <h1>Receita (mock)</h1>
      <div class="muted">Paciente: ${escapeHtml(pacienteNome)} • Consulta #${escapeHtml(m.consultaId)}</div>
      <div style="height:12px"></div>
      <div class="box">
        <div class="row"><b>Medicamento:</b> ${escapeHtml(m.nome)}</div>
        <div class="row"><b>Dosagem:</b> ${escapeHtml(m.dosagem || "—")}</div>
        <div class="row"><b>Frequência:</b> ${escapeHtml(m.frequencia || "—")}</div>
        <div class="row"><b>Via:</b> ${escapeHtml(m.via || "—")}</div>
      </div>
      <div style="height:24px"></div>
      <div class="muted">Assinatura: ________________________________</div>
      <script>window.print()</script>
    </body>
  </html>
  `;

  const w = window.open("", "_blank", "noopener,noreferrer,width=900,height=650");
  if (!w) return;
  w.document.open();
  w.document.write(html);
  w.document.close();
}

function escapeHtml(s: string) {
  return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;");
}