import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import  Input  from "../../../components/form/Input"; 
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";

import { seedPacientes } from "../../../mocks/db/seed";
import type { ConsultaDTO } from "../../../mocks/db/seed";
import { getConsultaById } from "../../../mocks/mappers";

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

export default function ConsultaEdit() {
  const navigate = useNavigate();
  const { id } = useParams();

  const initial = useMemo(() => (id ? getConsultaById(id) : null), [id]);
  const [form, setForm] = useState<ConsultaDTO | null>(initial);

  if (!form) {
    return (
      <div className="consultas-page">
        <PageHeader title="Editar consulta" subtitle="Não encontrada" />
      </div>
    );
  }

  return (
    <div className="consultas-page">
      <PageHeader
        title="Editar consulta"
        subtitle={form.id}
        actionLabel="Salvar"
        onAction={() => {
          console.log("Salvar edição (mock):", form);
          navigate(`/consultas/${form.id}`);
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
                setForm((s) => (s ? { ...s, pacienteId } : s));
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
              onChange={(e) =>
                setForm((s) => (s ? { ...s, dataHora: e.target.value } : s))
              }
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
                  if (!isDuracaoMinutos(n)) return; // ✅ sem any
                  setForm((s) => (s ? { ...s, duracaoMinutos: n } : s));
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
                  if (!isTipoConsulta(v)) return; // ✅ sem any
                  setForm((s) => (s ? { ...s, tipo: v } : s));
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
              onChange={(e) =>
                setForm((s) => (s ? { ...s, motivo: e.target.value } : s))
              }
              placeholder="Descreva a queixa principal..."
            />
          </label>

          <div className="consultas-actionsBottom">
            <PrimaryButton onClick={() => navigate(`/consultas/${form.id}`)}>
              Cancelar
            </PrimaryButton>

            <PrimaryButton
              onClick={() => {
                console.log("Salvar edição (mock):", form);
                navigate(`/consultas/${form.id}`);
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