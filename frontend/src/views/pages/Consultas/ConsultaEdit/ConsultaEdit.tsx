import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import DateTimeField from "../../../../components/form/DateTimeField/DateTimeField";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../../../../components/ui/HighlightButton/HighlightButton";

import { seedPacientes } from "../../../../mocks/db/seed";
import type { ConsultaDTO } from "../../../../mocks/db/seed";
import { getConsultaById, updateConsulta } from "../../../../mocks/db/storage";

import type {
  DuracaoMinutos,
  TipoConsulta,
  MeioPagamento,
} from "../../../../domain/enums/statusConsulta";

import "../base.css";
import "./styles.css";

const DURACOES: DuracaoMinutos[] = [15, 30, 45, 60];
const TIPOS: TipoConsulta[] = ["PRESENCIAL", "TELECONSULTA", "RETORNO"];
const MEIOS: MeioPagamento[] = ["PIX", "CARTAO", "DINHEIRO"];

const duracaoOptions: readonly SelectOption<DuracaoMinutos>[] = DURACOES.map((d) => ({
  value: d,
  label: `${d} min`,
}));

const tipoOptions: readonly SelectOption<TipoConsulta>[] = TIPOS.map((t) => ({
  value: t,
  label: t === "PRESENCIAL" ? "Presencial" : t === "TELECONSULTA" ? "Teleconsulta" : "Retorno",
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

function normalizeDateTimeLocal(value: string): string {
  if (!value) return "";
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) return value;
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value)) return value.slice(0, 16);
  return value;
}

function parseMoneyToNumber(input: string): number {
  const raw = (input ?? "").trim();
  if (!raw) return 0;

  const cleaned = raw.replace(/[R$\s]/g, "");

  if (cleaned.includes(",")) {
    const normalized = cleaned.replace(/\./g, "").replace(",", ".");
    const n = Number(normalized);
    return Number.isFinite(n) ? n : 0;
  }

  const n = Number(cleaned);
  return Number.isFinite(n) ? n : 0;
}

function formatMoneyBR(value: number): string {
  if (!Number.isFinite(value)) return "";
  return value.toFixed(2).replace(".", ",");
}

type ConsultaFormModel = {
  id: string;
  pacienteId: number;
  medicoId: string;
  medicoNome: string;

  dataHora: string;
  duracaoMinutos: DuracaoMinutos;
  tipo: TipoConsulta;
  status: ConsultaDTO["status"];

  motivo: string;

  valorConsulta: string;
  meioPagamento: MeioPagamento;
  pago: boolean;
  dataPagamento: string;
};

function dtoToForm(dto: ConsultaDTO): ConsultaFormModel {
  return {
    id: dto.id,
    pacienteId: dto.pacienteId,
    medicoId: dto.medicoId,
    medicoNome: dto.medicoNome,

    dataHora: normalizeDateTimeLocal(dto.dataHora),
    duracaoMinutos: dto.duracaoMinutos,
    tipo: dto.tipo,
    status: dto.status,

    motivo: dto.motivo ?? "",

    // ✅ defaults pq no DTO são opcionais
    valorConsulta: formatMoneyBR(dto.valorConsulta ?? 0),
    meioPagamento: dto.meioPagamento ?? "PIX",
    pago: dto.pago ?? false,
    dataPagamento: normalizeDateTimeLocal(dto.dataPagamento ?? ""),
  };
}

export default function ConsultaEdit() {
  const navigate = useNavigate();
  const { id } = useParams();

  const pacienteOptions = useMemo(() => toPacienteOptions(), []);

  const initialDto = useMemo(() => (id ? getConsultaById(id) : null), [id]);
  const [form, setForm] = useState<ConsultaFormModel | null>(() => {
    if (!initialDto) return null;
    return dtoToForm(initialDto);
  });

  if (!form) {
    return (
      <div className="consultas-page consultas-page--form">
        <PageHeader title="Editar consulta" subtitle="Não encontrada" />
      </div>
    );
  }

  // ✅ “congela” valores não-nulos pra usar em handlers sem TS reclamar
  const consultaId = form.id;

  function save(current: ConsultaFormModel) {
    const patch: Partial<ConsultaDTO> = {
      pacienteId: current.pacienteId,
      dataHora: normalizeDateTimeLocal(current.dataHora),
      duracaoMinutos: current.duracaoMinutos,
      tipo: current.tipo,
      motivo: current.motivo,

      valorConsulta: parseMoneyToNumber(current.valorConsulta),
      meioPagamento: current.meioPagamento,
      pago: current.pago,
      dataPagamento: current.pago
        ? normalizeDateTimeLocal(current.dataPagamento || current.dataHora)
        : "",
    };

    updateConsulta(consultaId, patch);
    navigate(`/consultas/${consultaId}`);
  }

  return (
    <div className="consultas-page consultas-page--form">
      <PageHeader title="Editar consulta" subtitle={consultaId} />

      <Card>
        <div className="consultas-formWrap">
          <div className="consultas-form">
            <div className="consultas-row2">
              <div className="consultas-field">
                <span className="consultas-label">Paciente</span>
                <SelectField<number>
                  value={form.pacienteId}
                  onChange={(pacienteId) =>
                    setForm((s) => (s ? { ...s, pacienteId } : s))
                  }
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
                  value={form.dataHora}
                  onChange={(dataHora) =>
                    setForm((s) =>
                      s ? { ...s, dataHora: normalizeDateTimeLocal(dataHora) } : s
                    )
                  }
                  step={60}
                  aria-label="Data e hora"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Duração</span>
                <SelectField<DuracaoMinutos>
                  value={form.duracaoMinutos}
                  onChange={(duracaoMinutos) =>
                    setForm((s) => (s ? { ...s, duracaoMinutos } : s))
                  }
                  options={duracaoOptions}
                  ariaLabel="Selecionar duração"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Tipo</span>
                <SelectField<TipoConsulta>
                  value={form.tipo}
                  onChange={(tipo) =>
                    setForm((s) => (s ? { ...s, tipo } : s))
                  }
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
                onChange={(e) =>
                  setForm((s) => (s ? { ...s, motivo: e.target.value } : s))
                }
                placeholder="Descreva a queixa principal..."
              />
            </div>

            <div className="consultas-divider" />

            <div className="consultas-rowPay">
              <div className="consultas-field">
                <span className="consultas-label">Valor (R$)</span>
                <Input
                  value={form.valorConsulta}
                  onChange={(e) =>
                    setForm((s) =>
                      s ? { ...s, valorConsulta: e.target.value } : s
                    )
                  }
                  placeholder="150,00"
                />
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Meio de pagamento</span>
                <SelectField<MeioPagamento>
                  value={form.meioPagamento}
                  onChange={(meioPagamento) =>
                    setForm((s) => (s ? { ...s, meioPagamento } : s))
                  }
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
                      setForm((s) =>
                        s
                          ? {
                              ...s,
                              pago: e.target.checked,
                              dataPagamento: e.target.checked
                                ? (s.dataPagamento || s.dataHora)
                                : "",
                            }
                          : s
                      )
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
                  value={form.dataPagamento}
                  onChange={(dataPagamento) =>
                    setForm((s) =>
                      s
                        ? {
                            ...s,
                            dataPagamento: normalizeDateTimeLocal(dataPagamento),
                          }
                        : s
                    )
                  }
                  step={60}
                  aria-label="Data do pagamento"
                />
              </div>
            ) : null}

            <div className="consultas-actionsBottom">
              <SecondaryButton onClick={() => navigate(`/consultas/${consultaId}`)}>
                Cancelar
              </SecondaryButton>

              <HighlightButton onClick={() => save(form)}>
                Salvar
              </HighlightButton>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
}