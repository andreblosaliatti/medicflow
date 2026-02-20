import { useEffect, useMemo, useState } from "react";

import Drawer from "../ui/Drawer/Drawer";
import SecondaryButton from "../ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../ui/HighlightButton/HighlightButton";
import Input from "../form/Input";
import SelectField from "../form/SelectField/SelectField";
import DateTimeField from "../form/DateTimeField/DateTimeField";

import type {
  DuracaoMinutos,
  MeioPagamento,
  StatusConsulta,
  TipoConsulta,
} from "../../domain/enums/statusConsulta";

import "./styles.css";

export type ConsultaDraft = {
  pacienteId: string;
  pacienteNome: string;

  medicoId: string;
  medicoNome: string;

  dataHora: string; // yyyy-mm-ddThh:mm
  duracaoMinutos: DuracaoMinutos;

  tipo: TipoConsulta;

  motivo: string;

  teleconsulta: boolean;
  linkAcesso: string;

  retorno: boolean;
  dataLimiteRetorno: string;

  valorConsulta: string;
  meioPagamento: MeioPagamento;
  pago: boolean;
  dataPagamento: string;

  status: StatusConsulta; // sempre AGENDADA no create
};

type Props = {
  open: boolean;
  mode: "create" | "edit";
  initialValue: ConsultaDraft | null;

  doctorId: string;
  doctorName: string;

  onClose: () => void;
  onSave: (data: ConsultaDraft) => void;
};

function toIsoLocal(d: Date) {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const hh = String(d.getHours()).padStart(2, "0");
  const mi = String(d.getMinutes()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
}

function emptyDraft(doctorId: string, doctorName: string): ConsultaDraft {
  const now = new Date();
  now.setMinutes(0, 0, 0);

  return {
    pacienteId: "",
    pacienteNome: "",

    medicoId: doctorId,
    medicoNome: doctorName,

    dataHora: toIsoLocal(now),
    duracaoMinutos: 30,

    tipo: "PRESENCIAL",
    status: "AGENDADA",

    motivo: "",

    teleconsulta: false,
    linkAcesso: "",

    retorno: false,
    dataLimiteRetorno: "",

    valorConsulta: "",
    meioPagamento: "PIX",
    pago: false,
    dataPagamento: "",
  };
}

const tipoOptions = [
  { value: "PRESENCIAL", label: "Presencial" },
  { value: "TELECONSULTA", label: "Teleconsulta" },
  { value: "RETORNO", label: "Retorno" },
] as const;

const duracaoOptions = [
  { value: 15, label: "15 min" },
  { value: 30, label: "30 min" },
  { value: 45, label: "45 min" },
  { value: 60, label: "60 min" },
] as const;

const pagamentoOptions = [
  { value: "PIX", label: "PIX" },
  { value: "CARTAO", label: "Cartão" },
  { value: "DINHEIRO", label: "Dinheiro" },
] as const;

export default function ConsultaFormDrawer({
  open,
  mode,
  initialValue,
  doctorId,
  doctorName,
  onClose,
  onSave,
}: Props) {
  useEffect(() => {
    if (!open) return;
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prev;
    };
  }, [open]);

  const base = useMemo<ConsultaDraft>(() => {
    const v = initialValue ?? emptyDraft(doctorId, doctorName);
    return {
      ...v,
      medicoId: doctorId,
      medicoNome: doctorName,
      status: mode === "create" ? "AGENDADA" : v.status,
    };
  }, [initialValue, doctorId, doctorName, mode]);

  const [form, setForm] = useState<ConsultaDraft>(base);

  const title = mode === "create" ? "Novo agendamento" : "Editar agendamento";

  function set<K extends keyof ConsultaDraft>(key: K, value: ConsultaDraft[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function submit() {
    const payload: ConsultaDraft = {
      ...form,
      status: mode === "create" ? "AGENDADA" : form.status,
      medicoId: doctorId,
      medicoNome: doctorName,
    };
    onSave(payload);
  }

  return (
    <Drawer
      open={open}
      onClose={onClose}
      title={title}
      variant="primary"
      headerClassName="consultaDrawerHeader"
      titleClassName="consultaDrawerTitle"
      closeClassName="consultaDrawerClose"
    >
      <div className="consultaDrawerForm">
        <div className="consultaDrawerGrid2">
          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Paciente</label>
            <Input
              value={form.pacienteNome}
              onChange={(e) => set("pacienteNome", e.target.value)}
              placeholder="Nome do paciente"
            />
          </div>

          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Médico</label>
            <Input value={doctorName} onChange={() => {}} disabled />
          </div>
        </div>

        <div className="consultaDrawerGrid2">
          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Data e hora</label>
            <DateTimeField
              value={form.dataHora}
              onChange={(v) => set("dataHora", v)}
              step={60}
              aria-label="Data e hora"
            />
          </div>

          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Duração</label>
            <SelectField<DuracaoMinutos>
              value={form.duracaoMinutos}
              onChange={(v) => set("duracaoMinutos", v)}
              placeholder="Duração"
              options={duracaoOptions}
            />
          </div>
        </div>

        <div className="consultaDrawerField">
          <label className="consultaDrawerLabel">Tipo</label>
          <SelectField<TipoConsulta>
            value={form.tipo}
            onChange={(v) => {
              set("tipo", v);
              set("teleconsulta", v === "TELECONSULTA");
              set("retorno", v === "RETORNO");
              if (v !== "TELECONSULTA") set("linkAcesso", "");
              if (v !== "RETORNO") set("dataLimiteRetorno", "");
            }}
            placeholder="Selecione"
            options={tipoOptions}
          />
        </div>

        {form.teleconsulta ? (
          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Link da teleconsulta</label>
            <Input
              value={form.linkAcesso}
              onChange={(e) => set("linkAcesso", e.target.value)}
              placeholder="https://..."
            />
          </div>
        ) : null}

        {form.retorno ? (
          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Data limite para retorno</label>
            <DateTimeField
              value={form.dataLimiteRetorno}
              onChange={(v) => set("dataLimiteRetorno", v)}
              step={60}
              aria-label="Data limite para retorno"
            />
          </div>
        ) : null}

        <div className="consultaDrawerField">
          <label className="consultaDrawerLabel">Motivo</label>
          <textarea
            className="consultaDrawerTextarea"
            value={form.motivo}
            onChange={(e) => set("motivo", e.target.value)}
            placeholder="Queixa principal / motivo..."
          />
        </div>

        <div className="consultaDrawerDivider" />

        <div className="consultaDrawerGrid3">
          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Valor (R$)</label>
            <Input
              value={form.valorConsulta}
              onChange={(e) => set("valorConsulta", e.target.value)}
              placeholder="150,00"
            />
          </div>

          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Meio de pagamento</label>
            <SelectField<MeioPagamento>
              value={form.meioPagamento}
              onChange={(v) => set("meioPagamento", v)}
              placeholder="Selecione"
              options={pagamentoOptions}
            />
          </div>

          <div className="consultaDrawerFieldInline">
            <label className="consultaDrawerCheckbox">
              <input
                type="checkbox"
                checked={form.pago}
                onChange={(e) => set("pago", e.target.checked)}
              />
              <span>Pago</span>
            </label>
          </div>
        </div>

        {form.pago ? (
          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Data do pagamento</label>
            <DateTimeField
              value={form.dataPagamento}
              onChange={(v) => set("dataPagamento", v)}
              step={60}
              aria-label="Data do pagamento"
            />
          </div>
        ) : null}

        <div className="consultaDrawerActions">
          <SecondaryButton onClick={onClose}>Cancelar</SecondaryButton>
          <HighlightButton onClick={submit}>Salvar</HighlightButton>
        </div>
      </div>
    </Drawer>
  );
}