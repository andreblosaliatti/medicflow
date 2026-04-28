import { useEffect, useMemo, useState } from "react";

import Drawer from "../ui/Drawer/Drawer";
import SecondaryButton from "../ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../ui/HighlightButton/HighlightButton";
import Input from "../form/Input";
import SelectField, { type SelectOption } from "../form/SelectField/SelectField";
import DateTimeField from "../form/DateTimeField/DateTimeField";

import type {
  DuracaoMinutos,
  MeioPagamento,
  StatusConsulta,
  TipoConsulta,
} from "../../domain/enums/statusConsulta";

import "./styles.css";

export type ConsultaDraft = {
  pacienteId: number | null;
  pacienteNome: string;
  medicoId: number;
  medicoNome: string;
  dataHora: string;
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
  status: StatusConsulta;
};

type Props = {
  open: boolean;
  mode: "create" | "edit";
  initialValue: ConsultaDraft | null;
  doctorId: number;
  doctorName: string;
  patientOptions: readonly SelectOption<number>[];
  lockPaciente?: boolean;
  isSaving?: boolean;
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

function emptyDraft(doctorId: number, doctorName: string): ConsultaDraft {
  const now = new Date();
  now.setMinutes(0, 0, 0);

  return {
    pacienteId: null,
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
  { value: "URGENCIA", label: "Urgência" },
] as const;

const duracaoOptions = [
  { value: 15, label: "15 min" },
  { value: 30, label: "30 min" },
  { value: 45, label: "45 min" },
  { value: 60, label: "60 min" },
] as const;

const pagamentoOptions = [
  { value: "DEBITO", label: "Cartão de débito" },
  { value: "CREDITO", label: "Cartão de crédito" },
  { value: "PIX", label: "PIX" },
  { value: "DINHEIRO", label: "Dinheiro" },
] as const;

export default function ConsultaFormDrawer({
  open,
  mode,
  initialValue,
  doctorId,
  doctorName,
  patientOptions,
  lockPaciente,
  isSaving = false,
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
    const value = initialValue ?? emptyDraft(doctorId, doctorName);
    return {
      ...value,
      medicoId: doctorId,
      medicoNome: doctorName,
      status: mode === "create" ? "AGENDADA" : value.status,
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

  const canSave = Boolean(form.pacienteId && form.motivo.trim() && !isSaving);

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
            <SelectField<number>
              value={form.pacienteId}
              onChange={(pacienteId) => {
                const option = patientOptions.find((item) => item.value === pacienteId) ?? null;
                setForm((prev) => ({
                  ...prev,
                  pacienteId,
                  pacienteNome: option?.label ?? prev.pacienteNome,
                }));
              }}
              options={patientOptions}
              placeholder="Selecione o paciente"
              ariaLabel="Paciente"
              disabled={Boolean(lockPaciente) || patientOptions.length === 0}
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
              onChange={(value) => set("dataHora", value)}
              step={60}
              aria-label="Data e hora"
            />
          </div>

          <div className="consultaDrawerField">
            <label className="consultaDrawerLabel">Duração</label>
            <SelectField<DuracaoMinutos>
              value={form.duracaoMinutos}
              onChange={(value) => set("duracaoMinutos", value)}
              placeholder="Duração"
              options={duracaoOptions}
            />
          </div>
        </div>

        <div className="consultaDrawerField">
          <label className="consultaDrawerLabel">Tipo</label>
          <SelectField<TipoConsulta>
            value={form.tipo}
            onChange={(value) => {
              set("tipo", value);
              set("teleconsulta", value === "TELECONSULTA");
              set("retorno", value === "RETORNO");
              if (value !== "TELECONSULTA") set("linkAcesso", "");
              if (value !== "RETORNO") set("dataLimiteRetorno", "");
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
              onChange={(value) => set("dataLimiteRetorno", value)}
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
              onChange={(value) => set("meioPagamento", value)}
              placeholder="Selecione"
              options={pagamentoOptions}
            />
          </div>

          <div className="consultaDrawerFieldInline">
            <label className="consultaDrawerCheckbox">
              <input
                type="checkbox"
                checked={form.pago}
                onChange={(e) =>
                  setForm((prev) => ({
                    ...prev,
                    pago: e.target.checked,
                    dataPagamento: e.target.checked ? (prev.dataPagamento || prev.dataHora) : "",
                  }))
                }
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
              onChange={(value) => set("dataPagamento", value)}
              step={60}
              aria-label="Data do pagamento"
            />
          </div>
        ) : null}

        <div className="consultaDrawerActions">
          <SecondaryButton onClick={onClose}>Cancelar</SecondaryButton>
          <HighlightButton onClick={submit} disabled={!canSave}>
            {isSaving ? "Salvando..." : "Salvar"}
          </HighlightButton>
        </div>
      </div>
    </Drawer>
  );
}
