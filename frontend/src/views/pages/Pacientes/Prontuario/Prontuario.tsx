import { useMemo } from "react";
import { useParams } from "react-router-dom";

import AppPage from "../../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";

import { getProntuarioByPacienteId } from "../../../../mocks/prontuario";

import "./styles.css";

function formatDateTime(iso: string) {
  const d = new Date(iso);
  return d.toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function ProntuarioPage() {
  const params = useParams();

  const pacienteId = useMemo(() => {
    const raw = params.id;
    if (!raw) return null;
    const n = Number(raw);
    return Number.isFinite(n) ? n : null;
  }, [params.id]);

  const prontuario = useMemo(() => {
    if (pacienteId === null) return null;
    return getProntuarioByPacienteId(pacienteId);
  }, [pacienteId]);

  if (pacienteId === null || !prontuario) {
    return (
      <AppPage
        className="prontuario-page"
        header={<PageHeader title="Prontuário" />}
        contentClassName="prontuario-content"
      >
        <Card>Paciente inválido ou não encontrado.</Card>
      </AppPage>
    );
  }

  const { pacienteNome, consultas } = prontuario;

  return (
    <AppPage
      className="prontuario-page"
      header={<PageHeader title={`Prontuário — ${pacienteNome}`} />}
      contentClassName="prontuario-content"
    >
      <div className="prontuario-timeline">
        {consultas.length === 0 ? (
          <Card>Nenhuma consulta encontrada.</Card>
        ) : (
          consultas.map((c) => (
            <Card key={c.id} className="prontuario-card">
              <div className="prontuario-head">
                <div>
                  <div className="prontuario-date">
                    {formatDateTime(c.dataHora)}
                  </div>
                  <div className="prontuario-medico">
                    {c.medicoNome}
                  </div>
                </div>

                <span className="prontuario-badge">
                  Consulta
                </span>
              </div>

              {/* Motivo */}
              <div className="prontuario-section">
                <div className="prontuario-label">Motivo</div>
                <div className="prontuario-text">
                  {c.motivo}
                </div>
              </div>

              {/* Anamnese */}
              {c.anamnese && (
                <div className="prontuario-section">
                  <div className="prontuario-label">Anamnese</div>
                  <div className="prontuario-text">
                    {c.anamnese}
                  </div>
                </div>
              )}

              {/* Diagnósticos */}
              {c.diagnosticos.length > 0 && (
                <div className="prontuario-section">
                  <div className="prontuario-label">
                    Diagnósticos
                  </div>
                  <ul className="prontuario-list">
                    {c.diagnosticos.map((d, idx) => (
                      <li key={idx}>
                        {d.codigo && (
                          <strong>{d.codigo}</strong>
                        )}
                        {d.codigo ? " — " : ""}
                        {d.descricao}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Medicações */}
              {c.medicacoes.length > 0 && (
                <div className="prontuario-section">
                  <div className="prontuario-label">
                    Medicações
                  </div>
                  <ul className="prontuario-list">
                    {c.medicacoes.map((m, idx) => (
                      <li key={idx}>
                        <strong>
                          {m.nome}
                          {m.dose
                            ? ` ${m.dose}`
                            : ""}
                        </strong>
                        {" — "}
                        {m.posologia}
                        {m.duracao
                          ? ` (${m.duracao})`
                          : ""}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Exames */}
              {c.exames.length > 0 && (
                <div className="prontuario-section">
                  <div className="prontuario-label">
                    Exames
                  </div>
                  <ul className="prontuario-list">
                    {c.exames.map((e, idx) => (
                      <li
                        key={idx}
                        className="prontuario-exame"
                      >
                        <span>{e.nome}</span>
                        <span
                          className={`prontuario-chip prontuario-chip-${e.status.toLowerCase()}`}
                        >
                          {e.status}
                        </span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {c.observacoes && (
                <div className="prontuario-section">
                  <div className="prontuario-label">Observações</div>
                  <div className="prontuario-text">{c.observacoes}</div>
                </div>
              )}
            </Card>
          ))
        )}
      </div>
    </AppPage>
  );
}