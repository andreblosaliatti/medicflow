import { useEffect, useMemo, useState } from "react";

import Drawer from "../ui/Drawer/Drawer";

import SecondaryButton from "../ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../ui/HighlightButton/HighlightButton";
import Input from "../form/Input";

import SelectField from "../form/SelectField/SelectField";

import type { DuracaoMinutos, MeioPagamento, StatusConsulta, TipoConsulta } from "../../domain/enums/statusConsulta";

import styles from "./appointment-detail-drawer.module.css";

export type ConsultaDraft = {
  // UI
  pacienteId: string;
  pacienteNome: string;

  // do contexto (médico logado)
  medicoId: string;
  medicoNome: string;

  // agendamento
  dataHora: string; // yyyy-mm-ddThh:mm
  duracaoMinutos: DuracaoMinutos;

  tipo: TipoConsulta;

  motivo: string;

  teleconsulta: boolean;
  linkAcesso: string;

  retorno: boolean;
  dataLimiteRetorno: string;

  valorConsulta: string; // string pra input
  meioPagamento: MeioPagamento;
  pago: boolean;
  dataPagamento: string;

  // controle interno
  status: StatusConsulta; // sempre AGENDADA no create
};

type Props = {
  open: boolean;
  mode: "create" | "edit";
  initialValue: ConsultaDraft | null;

  // ✅ vem do médico logado
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
  // ✅ trava scroll do fundo (body) quando abrir
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

    // garantia: médico sempre do logado
    const next: ConsultaDraft = {
      ...v,
      medicoId: doctorId,
      medicoNome: doctorName,
      // no create, força status AGENDADA
      status: mode === "create" ? "AGENDADA" : v.status,
    };

    return next;
  }, [initialValue, doctorId, doctorName, mode]);

  // ✅ sem effect de setState: o pai deve usar `key` pra resetar quando trocar
  const [form, setForm] = useState<ConsultaDraft>(base);

  const title = mode === "create" ? "Novo agendamento" : "Editar agendamento";

  function set<K extends keyof ConsultaDraft>(key: K, value: ConsultaDraft[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function submit() {
    const payload: ConsultaDraft = {
      ...form,
      // ✅ status não é editável na UI: garante no create
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
      headerClassName={styles.drawerHeader}
      titleClassName={styles.drawerTitle}
      closeClassName={styles.drawerClose}
    >
      <div className={styles.form}>
        {/* Paciente + Médico */}
        <div className={styles.grid2}>
          <div className={styles.field}>
            <label className={styles.label}>Paciente</label>
            <Input
              value={form.pacienteNome}
              onChange={(e) => set("pacienteNome", e.target.value)}
              placeholder="Nome do paciente"
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Médico</label>
            <Input value={doctorName} onChange={() => {}} disabled />
          </div>
        </div>

        {/* Data e duração */}
        <div className={styles.grid2}>
          <div className={styles.field}>
            <label className={styles.label}>Data e hora</label>

            {/* ✅ wrapper com ícone fixo (não some ao abrir o picker) */}
            <div className={styles.dtWrap}>
              <input
                className={styles.nativeInput}
                type="datetime-local"
                value={form.dataHora}
                onChange={(e) => set("dataHora", e.target.value)}
              />
              <span className={styles.dtIcon} aria-hidden="true">
                📅
              </span>
            </div>
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Duração</label>
            <SelectField<DuracaoMinutos>
              value={form.duracaoMinutos}
              onChange={(v) => set("duracaoMinutos", v)}
              placeholder="Duração"
              options={duracaoOptions}
            />
          </div>
        </div>

        {/* Tipo */}
        <div className={styles.field}>
          <label className={styles.label}>Tipo</label>
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
          <div className={styles.field}>
            <label className={styles.label}>Link da teleconsulta</label>
            <Input
              value={form.linkAcesso}
              onChange={(e) => set("linkAcesso", e.target.value)}
              placeholder="https://..."
            />
          </div>
        ) : null}

        {form.retorno ? (
          <div className={styles.field}>
            <label className={styles.label}>Data limite para retorno</label>

            <div className={styles.dtWrap}>
              <input
                className={styles.nativeInput}
                type="datetime-local"
                value={form.dataLimiteRetorno}
                onChange={(e) => set("dataLimiteRetorno", e.target.value)}
              />
              <span className={styles.dtIcon} aria-hidden="true">
                📅
              </span>
            </div>
          </div>
        ) : null}

        {/* Motivo */}
        <div className={styles.field}>
          <label className={styles.label}>Motivo</label>
          <textarea
            className={styles.textarea}
            value={form.motivo}
            onChange={(e) => set("motivo", e.target.value)}
            placeholder="Queixa principal / motivo..."
          />
        </div>

        <div className={styles.divider} />

        {/* Pagamento */}
        <div className={styles.grid3}>
          <div className={styles.field}>
            <label className={styles.label}>Valor (R$)</label>
            <Input
              value={form.valorConsulta}
              onChange={(e) => set("valorConsulta", e.target.value)}
              placeholder="150,00"
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Meio de pagamento</label>
            <SelectField<MeioPagamento>
              value={form.meioPagamento}
              onChange={(v) => set("meioPagamento", v)}
              placeholder="Selecione"
              options={pagamentoOptions}
            />
          </div>

          <div className={styles.fieldInline}>
            <label className={styles.checkbox}>
              <input type="checkbox" checked={form.pago} onChange={(e) => set("pago", e.target.checked)} />
              <span>Pago</span>
            </label>
          </div>
        </div>

        {form.pago ? (
          <div className={styles.field}>
            <label className={styles.label}>Data do pagamento</label>

            <div className={styles.dtWrap}>
              <input
                className={styles.nativeInput}
                type="datetime-local"
                value={form.dataPagamento}
                onChange={(e) => set("dataPagamento", e.target.value)}
              />
              <span className={styles.dtIcon} aria-hidden="true">
                📅
              </span>
            </div>
          </div>
        ) : null}

        <div className={styles.actions}>
          <SecondaryButton onClick={onClose}>Cancelar</SecondaryButton>
          <HighlightButton onClick={submit}>Salvar</HighlightButton>
        </div>
      </div>
    </Drawer>
  );
}