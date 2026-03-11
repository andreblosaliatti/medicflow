import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import AppPage from "../../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../../components/ui/Panel/Panel";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../../components/ui/Table/Table";

import { pacienteNomeById } from "../../../../mocks/mappers";
import {
  getExamesByPacienteId,
  getMedicamentosByPacienteId,
  seedPrescricoesIfEmpty,
  type ExameSolicitadoMock,
  type MedicamentoPrescritoMock,
  type StatusExame,
  updateExame,
  duplicateMedicamento,
} from "../../../../mocks/prescricoesStorage";

import "./styles.css";

type Tab = "MEDICAMENTOS" | "EXAMES";

export default function PrescricoesPage() {
  const navigate = useNavigate();
  const params = useParams();
  const pacienteId = Number(params.id);

  const [tab, setTab] = useState<Tab>("MEDICAMENTOS");

  // garante seed (sem depender do teu storage principal)
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

  return (
    <AppPage
      header={
        <PageHeader
          title="Prescrições"
          subtitle={pacienteNome}
        />
      }
    >
      <div className="mf-page-content prescricoes-page">
        <div className="prescricoes-tabs" role="tablist" aria-label="Prescrições">
          <button
            type="button"
            className={tab === "MEDICAMENTOS" ? "prescricoes-tab prescricoes-tab--active" : "prescricoes-tab"}
            onClick={() => setTab("MEDICAMENTOS")}
            role="tab"
            aria-selected={tab === "MEDICAMENTOS"}
          >
            Medicações ({medicamentos.length})
          </button>

          <button
            type="button"
            className={tab === "EXAMES" ? "prescricoes-tab prescricoes-tab--active" : "prescricoes-tab"}
            onClick={() => setTab("EXAMES")}
            role="tab"
            aria-selected={tab === "EXAMES"}
          >
            Exames ({exames.length})
          </button>
        </div>

        {tab === "MEDICAMENTOS" ? (
          <Panel title="Medicações prescritas" icon="💊">
            <MedicamentosTable
              rows={medicamentos}
              onOpenConsulta={(consultaId) => navigate(`/consultas/${consultaId}`)}
              onRepeat={(id) => {
                duplicateMedicamento(id);
                // força re-render simples (mock): troca tab ida/volta
                setTab("EXAMES");
                setTab("MEDICAMENTOS");
              }}
              onPrint={(m) => printMedicamento(pacienteNome, m)}
            />
            <div className="prescricoes-hint mf-muted">
        
            </div>
          </Panel>
        ) : (
          <Panel title="Exames solicitados" icon="🧪">
            <ExamesTable
              rows={exames}
              onOpenConsulta={(consultaId) => navigate(`/consultas/${consultaId}`)}
              onChangeStatus={(id, status) => {
                // regra simples de mock:
                // - REALIZADO seta dataColeta se vazio
                // - CANCELADO limpa datas
                updateExame(id, (prev) => {
                  const now = new Date().toISOString();
                  const next: Partial<ExameSolicitadoMock> = { status };

                  if (status === "REALIZADO") {
                    next.dataColeta = prev.dataColeta ?? now;
                  }
                  if (status === "CANCELADO") {
                    next.dataColeta = null;
                    next.dataResultado = null;
                  }
                  return next;
                });

                setTab("MEDICAMENTOS");
                setTab("EXAMES");
              }}
              onSetColeta={(id) => {
                updateExame(id, () => ({ dataColeta: new Date().toISOString() }));
                setTab("MEDICAMENTOS");
                setTab("EXAMES");
              }}
              onSetResultado={(id) => {
                updateExame(id, () => ({ dataResultado: new Date().toISOString() }));
                setTab("MEDICAMENTOS");
                setTab("EXAMES");
              }}
            />
          </Panel>
        )}
      </div>
    </AppPage>
  );
}

function MedicamentosTable({
  rows,
  onOpenConsulta,
  onRepeat,
  onPrint,
}: {
  rows: MedicamentoPrescritoMock[];
  onOpenConsulta: (consultaId: string) => void;
  onRepeat: (id: number) => void;
  onPrint: (m: MedicamentoPrescritoMock) => void;
}) {
  return (
    <TableWrap>
      <Table>
        <THead>
          <tr>
            <Th>Medicamento</Th>
            <Th style={{ width: 160 }}>Dosagem</Th>
            <Th style={{ width: 180 }}>Frequência</Th>
            <Th style={{ width: 120 }}>Via</Th>
            <Th style={{ width: 220 }} align="right">
              Ações
            </Th>
          </tr>
        </THead>

        <TBody>
          {rows.length === 0 ? (
            <Tr>
              <Td className="prescricoes-empty" colSpan={5}>
                Nenhuma medicação registrada.
              </Td>
            </Tr>
          ) : (
            rows.map((m) => (
              <Tr key={m.id} onClick={() => onOpenConsulta(m.consultaId)} ariaLabel={`Abrir consulta da medicação ${m.nome}`}>
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
                  <div className="prescricoes-actions">
                    <button
                      type="button"
                      className="prescricoes-btn"
                      onClick={() => onOpenConsulta(m.consultaId)}
                    >
                      Ver consulta
                    </button>

                    <button
                      type="button"
                      className="prescricoes-btn prescricoes-btn--primary"
                      onClick={() => onRepeat(m.id)}
                      title="Duplica a medicação (mock)"
                    >
                      Repetir
                    </button>

                    <button
                      type="button"
                      className="prescricoes-btn"
                      onClick={() => onPrint(m)}
                      title="Impressão mock"
                    >
                      Imprimir
                    </button>
                  </div>
                </Td>
              </Tr>
            ))
          )}
        </TBody>
      </Table>
    </TableWrap>
  );
}

function ExamesTable({
  rows,
  onOpenConsulta,
  onChangeStatus,
  onSetColeta,
  onSetResultado,
}: {
  rows: ExameSolicitadoMock[];
  onOpenConsulta: (consultaId: string) => void;
  onChangeStatus: (id: number, status: StatusExame) => void;
  onSetColeta: (id: number) => void;
  onSetResultado: (id: number) => void;
}) {
  return (
    <TableWrap>
      <Table>
        <THead>
          <tr>
            <Th>Exame</Th>
            <Th style={{ width: 150 }}>Status</Th>
            <Th style={{ width: 160 }}>Coleta</Th>
            <Th style={{ width: 160 }}>Resultado</Th>
            <Th style={{ width: 260 }} align="right">
              Ações
            </Th>
          </tr>
        </THead>

        <TBody>
          {rows.length === 0 ? (
            <Tr>
              <Td className="prescricoes-empty" colSpan={5}>
                Nenhum exame registrado.
              </Td>
            </Tr>
          ) : (
            rows.map((x) => (
              <Tr key={x.id} onClick={() => onOpenConsulta(x.consultaId)} ariaLabel={`Abrir consulta do exame ${x.nome}`}>
                <Td>
                  <div className="prescricoes-main">
                    <div className="prescricoes-title">{x.nome}</div>
                    <div className="prescricoes-sub mf-muted">Consulta #{x.consultaId}</div>
                    {x.justificativa ? (
                      <div className="prescricoes-sub mf-muted">Justificativa: {x.justificativa}</div>
                    ) : null}
                  </div>
                </Td>

                <Td>
                  <select
                    className="prescricoes-select"
                    value={x.status}
                    onClick={(e) => e.stopPropagation()}
                    onChange={(e) => onChangeStatus(x.id, e.target.value as StatusExame)}
                    aria-label="Status do exame"
                  >
                    <option value="SOLICITADO">SOLICITADO</option>
                    <option value="AGENDADO">AGENDADO</option>
                    <option value="REALIZADO">REALIZADO</option>
                    <option value="CANCELADO">CANCELADO</option>
                  </select>
                </Td>

                <Td className="mf-mono">{formatISOToBR(x.dataColeta) ?? "—"}</Td>
                <Td className="mf-mono">{formatISOToBR(x.dataResultado) ?? "—"}</Td>

                <Td align="right" onClick={(e) => e.stopPropagation()}>
                  <div className="prescricoes-actions">
                    <button type="button" className="prescricoes-btn" onClick={() => onOpenConsulta(x.consultaId)}>
                      Ver consulta
                    </button>

                    <button type="button" className="prescricoes-btn" onClick={() => onSetColeta(x.id)}>
                      Marcar coleta (hoje)
                    </button>

                    <button type="button" className="prescricoes-btn prescricoes-btn--primary" onClick={() => onSetResultado(x.id)}>
                      Marcar resultado (hoje)
                    </button>
                  </div>
                </Td>
              </Tr>
            ))
          )}
        </TBody>
      </Table>
    </TableWrap>
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