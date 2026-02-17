import { useState } from "react";
import { useNavigate } from "react-router-dom";

import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import Input from "../../../components/form/Input";
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";

import { seedPacientes, MEDICO_SEED } from "../../../mocks/db/seed";
import type { ConsultaDTO } from "../../../mocks/db/seed";

import type { DuracaoMinutos, TipoConsulta } from "../../../domain/enums/statusConsulta";

import "./styles.css";

const DURACOES: DuracaoMinutos[] = [15, 30, 45, 60];
const TIPOS: TipoConsulta[] = ["PRESENCIAL", "TELECONSULTA", "RETORNO"];

function isDuracaoMinutos(v: number): v is DuracaoMinutos {
  return (DURACOES as number[]).includes(v);
}

function isTipoConsulta(v: string): v is TipoConsulta {
  return (TIPOS as string[]).includes(v);
}

// ✅ id estável por sessão de tela (sem Math.random e sem warning de impure)
// (se você quiser id “real”, eu te mostro como pegar maxId do storage)
function createLocalId() {
  const n = Date.now().toString(36);
  return `c-${n}`;
}

export default function ConsultaNew() {
  const navigate = useNavigate();

  const [form, setForm] = useState<ConsultaDTO>(() => {
    const nextId = createLocalId();

    return {
      id: nextId,
      pacienteId: seedPacientes[0]?.id ?? 1,
      medicoId: MEDICO_SEED.id,
      medicoNome: MEDICO_SEED.nome,
      dataHora: "2026-02-20T09:00",
      duracaoMinutos: 30,
      tipo: "PRESENCIAL",
      status: "AGENDADA",
      motivo: "",
      sala: "Sala 01",
      telefoneContato: "",
      valorConsulta: 0,
      pago: false,
      meioPagamento: "PIX",
    };
  });

  return (
    <div className="consultas-page">
      <PageHeader
        title="Nova consulta"
        subtitle="Criar agendamento"
        actionLabel="Salvar"
        onAction={() => {
          console.log("Salvar (mock):", form);
          navigate("/consultas");
        }}
      />

      <Card>
        <div className="consultas-form">
          <label className="consultas-field">
            <span className="consultas-label">Paciente</span>
            <select
              className="consultas-select"
              value={form.pacienteId}
              onChange={(e) => {
                const pacienteId = Number(e.target.value);
                setForm((s) => ({ ...s, pacienteId }));
              }}
            >
              {seedPacientes.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.primeiroNome} {p.sobrenome}
                </option>
              ))}
            </select>
          </label>

          <label className="consultas-field">
            <span className="consultas-label">Médico</span>
            <Input value={form.medicoNome} disabled />
          </label>

          <label className="consultas-field">
            <span className="consultas-label">Data/Hora</span>
            <Input
              value={form.dataHora}
              onChange={(e) => setForm((s) => ({ ...s, dataHora: e.target.value }))}
              placeholder="yyyy-mm-ddTHH:mm"
            />
          </label>

          <div className="consultas-row2">
            <label className="consultas-field">
              <span className="consultas-label">Duração</span>
              <select
                className="consultas-select"
                value={form.duracaoMinutos}
                onChange={(e) => {
                  const n = Number(e.target.value);
                  if (!isDuracaoMinutos(n)) return;
                  setForm((s) => ({ ...s, duracaoMinutos: n }));
                }}
              >
                {DURACOES.map((d) => (
                  <option key={d} value={d}>
                    {d} min
                  </option>
                ))}
              </select>
            </label>

            <label className="consultas-field">
              <span className="consultas-label">Tipo</span>
              <select
                className="consultas-select"
                value={form.tipo}
                onChange={(e) => {
                  const v = e.target.value;
                  if (!isTipoConsulta(v)) return;
                  setForm((s) => ({ ...s, tipo: v }));
                }}
              >
                {TIPOS.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <label className="consultas-field">
            <span className="consultas-label">Motivo</span>
            <textarea
              className="consultas-textarea"
              value={form.motivo ?? ""}
              onChange={(e) => setForm((s) => ({ ...s, motivo: e.target.value }))}
              placeholder="Descreva a queixa principal..."
            />
          </label>

          <div className="consultas-actionsBottom">
            <PrimaryButton onClick={() => navigate("/consultas")}>Cancelar</PrimaryButton>
            <PrimaryButton
              onClick={() => {
                console.log("Salvar (mock):", form);
                navigate("/consultas");
              }}
            >
              Salvar
            </PrimaryButton>
          </div>
        </div>
      </Card>
    </div>
  );
}