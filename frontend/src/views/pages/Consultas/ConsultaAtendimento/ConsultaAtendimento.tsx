import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import PrimaryButton from "../../../../components/ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";

import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";

import {
  getConsultaById,
  iniciarAtendimento,
  finalizarAtendimento,
  getRegistroClinico,
  salvarRegistroClinico,
  pacienteNomeById,
} from "../../../../mocks/mappers";

import type {
  RegistroClinicoDTO,
  RegistroExameDTO,
  RegistroMedicacaoDTO,
} from "../../../../mocks/db/registroClinico.seed";

import "./styles.css";
import "../base.css";

type Tab = "ANAMNESE" | "EXAME_FISICO" | "DIAGNOSTICO" | "PRESCRICAO" | "EXAMES" | "OBS";

function makeId(prefix: string) {
  return `${prefix}-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
}

const tabOptions: readonly { key: Tab; label: string }[] = [
  { key: "ANAMNESE", label: "Anamnese" },
  { key: "EXAME_FISICO", label: "Exame físico" },
  { key: "DIAGNOSTICO", label: "Diagnóstico" },
  { key: "PRESCRICAO", label: "Prescrição" },
  { key: "EXAMES", label: "Exames" },
  { key: "OBS", label: "Observações" },
] as const;

const statusExameOptions: readonly SelectOption<RegistroExameDTO["status"]>[] = [
  { value: "SOLICITADO", label: "Solicitado" },
  { value: "REALIZADO", label: "Realizado" },
  { value: "RESULTADO", label: "Resultado" },
] as const;

const viaOptions: readonly SelectOption<string>[] = [
  { value: "VO", label: "VO" },
  { value: "SL", label: "SL" },
  { value: "IM", label: "IM" },
  { value: "IV", label: "IV" },
  { value: "SC", label: "SC" },
  { value: "TOP", label: "Tópico" },
  { value: "INAL", label: "Inalatória" },
] as const;

export default function ConsultaAtendimento() {
  const navigate = useNavigate();

  const { id } = useParams();
  const consultaId = id; // string | undefined

  // Hooks SEMPRE executam, sem return antes deles
  const consulta = useMemo(() => {
    if (!consultaId) return null;
    return getConsultaById(consultaId);
  }, [consultaId]);

  const [tab, setTab] = useState<Tab>("ANAMNESE");
  const [reg, setReg] = useState<RegistroClinicoDTO | null>(null);
  const [saving, setSaving] = useState(false);
  const [medQuery, setMedQuery] = useState("");
  const [exameQuery, setExameQuery] = useState("");



const safeConsultaId = consultaId ?? ""; 
  useEffect(() => {
  if (!consultaId) return;

  iniciarAtendimento(consultaId);

  const r = getRegistroClinico(consultaId);

  setReg({
    ...r,
    consultaId,
    medicacoes: r.medicacoes ?? [],
    exames: r.exames ?? [],
    anamnese: r.anamnese ?? "",
    exameFisico: r.exameFisico ?? "",
    diagnostico: r.diagnostico ?? "",
    prescricaoTexto: r.prescricaoTexto ?? "",
    observacoes: r.observacoes ?? "",
    updatedAt: r.updatedAt ?? new Date().toISOString(),
  });
}, [consultaId]);

  // Agora sim: renderização condicional pode acontecer aqui embaixo

  if (!consultaId) {
    return (
      <div className="consultas-page">
        <PageHeader title="Atendimento" subtitle="Consulta inválida" />
      </div>
    );
  }

  if (!consulta) {
    return (
      <div className="consultas-page">
        <PageHeader title="Atendimento" subtitle="Consulta não encontrada" />
        <Card>
          <div style={{ padding: 14 }}>
            <PrimaryButton onClick={() => navigate("/consultas")}>Voltar</PrimaryButton>
          </div>
        </Card>
      </div>
    );
  }

  const pacienteNome = pacienteNomeById(consulta.pacienteId);

  function handleSave() {
    if (!reg) return;

    setSaving(true);
    try {
      const payload: RegistroClinicoDTO = {
        ...reg,
        consultaId: safeConsultaId,
        medicacoes: reg.medicacoes ?? [],
        exames: reg.exames ?? [],
        updatedAt: new Date().toISOString(),
      };

      const saved = salvarRegistroClinico(payload);

      setReg({
        ...saved,
        consultaId: safeConsultaId,
        medicacoes: saved.medicacoes ?? [],
        exames: saved.exames ?? [],
        updatedAt: saved.updatedAt ?? new Date().toISOString(),
      });
    } finally {
      setSaving(false);
    }
  }

  function handleFinalizar() {
  if (!consultaId) return; 

  handleSave();
  finalizarAtendimento(consultaId);
  navigate(`/consultas/${consultaId}`);
}

  function addMedicacao() {
    const nome = medQuery.trim();
    if (!nome) return;

    const next: RegistroMedicacaoDTO = {
      id: makeId("med"),
      nome,
      dosagem: "",
      frequencia: "",
      via: "VO",
    };

    setReg((s) => (s ? { ...s, medicacoes: [...(s.medicacoes ?? []), next] } : s));
    setMedQuery("");
  }

  function updateMedicacao(medId: string, patch: Partial<RegistroMedicacaoDTO>) {
    setReg((s) => {
      if (!s) return s;
      const next = (s.medicacoes ?? []).map((m) => (m.id === medId ? { ...m, ...patch } : m));
      return { ...s, medicacoes: next };
    });
  }

  function removeMedicacao(medId: string) {
    setReg((s) => {
      if (!s) return s;
      return { ...s, medicacoes: (s.medicacoes ?? []).filter((m) => m.id !== medId) };
    });
  }

  function addExame() {
    const nome = exameQuery.trim();
    if (!nome) return;

    const next: RegistroExameDTO = {
      id: makeId("ex"),
      nome,
      status: "SOLICITADO",
      justificativa: "",
      observacoes: "",
    };

    setReg((s) => (s ? { ...s, exames: [...(s.exames ?? []), next] } : s));
    setExameQuery("");
  }

  function updateExame(exId: string, patch: Partial<RegistroExameDTO>) {
    setReg((s) => {
      if (!s) return s;
      const next = (s.exames ?? []).map((e) => (e.id === exId ? { ...e, ...patch } : e));
      return { ...s, exames: next };
    });
  }

  function removeExame(exId: string) {
    setReg((s) => {
      if (!s) return s;
      return { ...s, exames: (s.exames ?? []).filter((e) => e.id !== exId) };
    });
  }

  if (!reg) {
    return (
      <div className="consultas-page">
        <PageHeader title={`Atendimento • ${pacienteNome}`} subtitle="Carregando..." />
      </div>
    );
  }

  return (
    <div className="consultas-page">
      <PageHeader
        title={`Atendimento • ${pacienteNome}`}
        rightSlot={
          <div className="atd-meta">
            <span className="atd-pill">{consulta.dataHora}</span>
            <span className="atd-pill">{consulta.tipo}</span>
            <span className="atd-pill">{consulta.status}</span>
          </div>
        }
        actions={[
          { label: saving ? "Salvando..." : "Salvar", variant: "highlight", onClick: handleSave },
          { label: "Finalizar", variant: "primary", onClick: handleFinalizar },
        ]}
      />

      <Card>
        <div className="atd-tabs">
          {tabOptions.map((t) => (
            <button
              key={t.key}
              type="button"
              className={`atd-tab ${tab === t.key ? "is-active" : ""}`}
              onClick={() => setTab(t.key)}
            >
              {t.label}
            </button>
          ))}
        </div>

        <div className="atd-panel">
          {tab === "ANAMNESE" ? (
            <label className="atd-field">
              <span className="atd-label">Anamnese</span>
              <textarea
                className="atd-textarea"
                value={reg.anamnese ?? ""}
                onChange={(e) => setReg((s) => (s ? { ...s, anamnese: e.target.value } : s))}
                placeholder="História clínica, queixa, antecedentes..."
              />
            </label>
          ) : null}

          {tab === "EXAME_FISICO" ? (
            <label className="atd-field">
              <span className="atd-label">Exame físico</span>
              <textarea
                className="atd-textarea"
                value={reg.exameFisico ?? ""}
                onChange={(e) => setReg((s) => (s ? { ...s, exameFisico: e.target.value } : s))}
                placeholder="Sinais vitais, inspeção, palpação, ausculta..."
              />
            </label>
          ) : null}

          {tab === "DIAGNOSTICO" ? (
            <label className="atd-field">
              <span className="atd-label">Diagnóstico</span>
              <textarea
                className="atd-textarea"
                value={reg.diagnostico ?? ""}
                onChange={(e) => setReg((s) => (s ? { ...s, diagnostico: e.target.value } : s))}
                placeholder="CID, hipótese diagnóstica, diagnóstico final..."
              />
            </label>
          ) : null}

          {tab === "PRESCRICAO" ? (
            <>
              <div className="atd-inlineAdd">
                <div className="atd-grow">
                  <Input
                    value={medQuery}
                    onChange={(e) => setMedQuery(e.target.value)}
                    placeholder="Digite o nome do medicamento e clique em Adicionar…"
                  />
                </div>
                <PrimaryButton onClick={addMedicacao}>Adicionar</PrimaryButton>
              </div>

              <div className="atd-list">
                {(reg.medicacoes ?? []).length === 0 ? (
                  <div className="atd-empty">Nenhuma medicação adicionada.</div>
                ) : (
                  (reg.medicacoes ?? []).map((m) => (
                    <div key={m.id} className="atd-item">
                      <div className="atd-itemHeader">
                        <strong className="atd-itemTitle">{m.nome}</strong>
                        <button type="button" className="atd-remove" onClick={() => removeMedicacao(m.id)}>
                          Remover
                        </button>
                      </div>

                      <div className="atd-grid2">
                        <Input
                          value={m.dosagem ?? ""}
                          onChange={(e) => updateMedicacao(m.id, { dosagem: e.target.value })}
                          placeholder="Dosagem (ex: 500mg)"
                        />

                        <SelectField<string>
                          label="Via"
                          value={(m.via ?? "VO") as string}
                          onChange={(v) => updateMedicacao(m.id, { via: v })}
                          options={viaOptions}
                          placeholder="Via..."
                        />
                      </div>

                      <Input
                        value={m.frequencia ?? ""}
                        onChange={(e) => updateMedicacao(m.id, { frequencia: e.target.value })}
                        placeholder="Frequência / posologia (ex: 8/8h por 5 dias)"
                      />
                    </div>
                  ))
                )}
              </div>

              <label className="atd-field">
                <span className="atd-label">Prescrição (texto livre)</span>
                <textarea
                  className="atd-textarea"
                  value={reg.prescricaoTexto ?? ""}
                  onChange={(e) => setReg((s) => (s ? { ...s, prescricaoTexto: e.target.value } : s))}
                  placeholder="Se quiser registrar em texto corrido..."
                />
              </label>
            </>
          ) : null}

          {tab === "EXAMES" ? (
            <>
              <div className="atd-inlineAdd">
                <div className="atd-grow">
                  <Input
                    value={exameQuery}
                    onChange={(e) => setExameQuery(e.target.value)}
                    placeholder="Digite o nome do exame e clique em Adicionar…"
                  />
                </div>
                <PrimaryButton onClick={addExame}>Adicionar</PrimaryButton>
              </div>

              <div className="atd-list">
                {(reg.exames ?? []).length === 0 ? (
                  <div className="atd-empty">Nenhum exame adicionado.</div>
                ) : (
                  (reg.exames ?? []).map((ex) => (
                    <div key={ex.id} className="atd-item">
                      <div className="atd-itemHeader">
                        <strong className="atd-itemTitle">{ex.nome}</strong>
                        <button type="button" className="atd-remove" onClick={() => removeExame(ex.id)}>
                          Remover
                        </button>
                      </div>

                      <div className="atd-grid2">
                        <SelectField<RegistroExameDTO["status"]>
                          label="Status"
                          value={ex.status}
                          onChange={(v) => updateExame(ex.id, { status: v })}
                          options={statusExameOptions}
                          placeholder="Status..."
                        />

                        <Input
                          value={ex.justificativa ?? ""}
                          onChange={(e) => updateExame(ex.id, { justificativa: e.target.value })}
                          placeholder="Justificativa"
                        />
                      </div>

                      <label className="atd-field">
                        <span className="atd-label">Observações</span>
                        <textarea
                          className="atd-textarea"
                          value={ex.observacoes ?? ""}
                          onChange={(e) => updateExame(ex.id, { observacoes: e.target.value })}
                          placeholder="Observações do exame..."
                        />
                      </label>
                    </div>
                  ))
                )}
              </div>
            </>
          ) : null}

          {tab === "OBS" ? (
            <label className="atd-field">
              <span className="atd-label">Observações gerais</span>
              <textarea
                className="atd-textarea"
                value={reg.observacoes ?? ""}
                onChange={(e) => setReg((s) => (s ? { ...s, observacoes: e.target.value } : s))}
                placeholder="Condutas, orientações, retorno..."
              />
            </label>
          ) : null}

          <div className="atd-actionsBottom">
            <SecondaryButton onClick={() => navigate(`/consultas/${consultaId}`)}>Voltar</SecondaryButton>
            <PrimaryButton onClick={handleSave}>Salvar</PrimaryButton>
          </div>
        </div>
      </Card>
    </div>
  );
}