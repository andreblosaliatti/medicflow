import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import DateTimeField from "../../../../components/form/DateTimeField/DateTimeField";

import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../../../../components/ui/HighlightButton/HighlightButton";

import { seedPacientes } from "../../../../mocks/db/seed";

import type {
  DuracaoMinutos,
  MeioPagamento,
  TipoConsulta,
} from "../../../../domain/enums/statusConsulta";

import "../base.css";
import "./styles.css";
import Panel from "../../../../components/ui/Panel/Panel";

type ConsultaCreateForm = {
  pacienteId: number;
  medicoNome: string;

  dataHora: string;
  duracaoMinutos: DuracaoMinutos;
  tipo: TipoConsulta;
  motivo: string;

  valorConsulta: string;
  meioPagamento: MeioPagamento;
  pago: boolean;
  dataPagamento: string;
};

const DURACOES: DuracaoMinutos[] = [15, 30, 45, 60];
const TIPOS: TipoConsulta[] = ["PRESENCIAL", "TELECONSULTA", "RETORNO"];
const MEIOS: MeioPagamento[] = ["PIX", "CARTAO", "DINHEIRO"];

const duracaoOptions: readonly SelectOption<DuracaoMinutos>[] = DURACOES.map((d) => ({
  value: d,
  label: `${d} min`,
}));

const tipoOptions: readonly SelectOption<TipoConsulta>[] = TIPOS.map((t) => ({
  value: t,
  label: t,
}));

const pagamentoOptions: readonly SelectOption<MeioPagamento>[] = MEIOS.map((m) => ({
  value: m,
  label: m === "CARTAO" ? "Cartão" : m === "DINHEIRO" ? "Dinheiro" : "PIX",
}));

function toPacienteOptions(): readonly SelectOption<number>[] {
  return seedPacientes.map((p) => ({
    value: p.id,
    label: `${p.primeiroNome} ${p.sobrenome}`,
  }));
}

function toIsoLocal(d: Date) {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const hh = String(d.getHours()).padStart(2, "0");
  const mi = String(d.getMinutes()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
}

function normalizeDateTimeLocal(value: string): string {
  if (!value) return "";
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) return value;
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value)) return value.slice(0, 16);
  return value;
}

export default function ConsultaCreate() {
  const navigate = useNavigate();
  const pacienteOptions = useMemo(() => toPacienteOptions(), []);

  const [form, setForm] = useState<ConsultaCreateForm>(() => {
    const now = new Date();
    now.setMinutes(0, 0, 0);

    return {
      pacienteId: seedPacientes[0]?.id ?? 1,
      medicoNome: "Dr. João Carvalho",

      dataHora: toIsoLocal(now),
      duracaoMinutos: 30,
      tipo: "PRESENCIAL",
      motivo: "",

      valorConsulta: "",
      meioPagamento: "PIX",
      pago: false,
      dataPagamento: "",
    };
  });

  function save() {
    console.log("Criar consulta (mock):", form);
    navigate("/consultas");
  }

  return (
    <div className="consultas-page consultas-page--form">
      <PageHeader title="Nova consulta"/>

      <Panel title="">
        <div className="consultas-formWrap">
          <div className="consultas-form">
            <div className="consultas-row2">
              <div className="consultas-field">
                <span className="consultas-label">Paciente</span>
                <SelectField<number>
                  value={form.pacienteId}
                  onChange={(pacienteId) => setForm((s) => ({ ...s, pacienteId }))}
                  options={pacienteOptions}
                  ariaLabel="Selecionar paciente"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Médico</span>
                <Input value={form.medicoNome} disabled />
              </div>
            </div>

            <div className="consultas-row3">
              <div className="consultas-field">
                <span className="consultas-label">Data/Hora</span>
                <DateTimeField
                  value={normalizeDateTimeLocal(form.dataHora)}
                  onChange={(dataHora) => setForm((s) => ({ ...s, dataHora: normalizeDateTimeLocal(dataHora) }))}
                  step={60}
                  aria-label="Data e hora"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Duração</span>
                <SelectField<DuracaoMinutos>
                  value={form.duracaoMinutos}
                  onChange={(duracaoMinutos) => setForm((s) => ({ ...s, duracaoMinutos }))}
                  options={duracaoOptions}
                  ariaLabel="Selecionar duração"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Tipo</span>
                <SelectField<TipoConsulta>
                  value={form.tipo}
                  onChange={(tipo) => setForm((s) => ({ ...s, tipo }))}
                  options={tipoOptions}
                  ariaLabel="Selecionar tipo"
                />
              </div>
            </div>

            <div className="consultas-field">
              <span className="consultas-label">Motivo</span>
              <textarea
                className="consultas-textarea"
                value={form.motivo}
                onChange={(e) => setForm((s) => ({ ...s, motivo: e.target.value }))}
                placeholder="Descreva a queixa principal..."
              />
            </div>

            <div className="consultas-divider" />

            {/* Pagamento */}
            <div className="consultas-rowPay">
              <div className="consultas-field">
                <span className="consultas-label">Valor (R$)</span>
                <Input
                  value={form.valorConsulta}
                  onChange={(e) => setForm((s) => ({ ...s, valorConsulta: e.target.value }))}
                  placeholder="150,00"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Meio de pagamento</span>
                <SelectField<MeioPagamento>
                  value={form.meioPagamento}
                  onChange={(meioPagamento) => setForm((s) => ({ ...s, meioPagamento }))}
                  options={pagamentoOptions}
                  ariaLabel="Selecionar meio de pagamento"
                />
              </div>

              <div className="consultas-fieldInline">
                <label className="consultas-checkbox">
                  <input
                    type="checkbox"
                    checked={form.pago}
                    onChange={(e) =>
                      setForm((s) => ({
                        ...s,
                        pago: e.target.checked,
                        dataPagamento: e.target.checked ? (s.dataPagamento || s.dataHora) : "",
                      }))
                    }
                  />
                  <span>Pago</span>
                </label>
              </div>
            </div>

            {form.pago ? (
              <div className="consultas-field">
                <span className="consultas-label">Data do pagamento</span>
                <DateTimeField
                  value={normalizeDateTimeLocal(form.dataPagamento)}
                  onChange={(v) => setForm((s) => ({ ...s, dataPagamento: normalizeDateTimeLocal(v) }))}
                  step={60}
                  aria-label="Data do pagamento"
                />
              </div>
            ) : null}

            <div className="consultas-actionsBottom">
              <SecondaryButton onClick={() => navigate("/consultas")}>Cancelar</SecondaryButton>
              <HighlightButton onClick={save}>Salvar</HighlightButton>
            </div>
          </div>
        </div>
      </Panel>
    </div>
  );
}