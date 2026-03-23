import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { useConsultaDetailsQuery, useConsultaMetadataQuery, useUpdateConsultaMutation } from "../../../../api/consultas/hooks";
import type { ConsultaDetailsViewModel, ConsultaUpdatePayload } from "../../../../api/consultas/types";
import { usePacientesQuery } from "../../../../api/pacientes/hooks";
import type { DuracaoMinutos, MeioPagamento, TipoConsulta } from "../../../../domain/enums/statusConsulta";
import { canEditConsulta } from "../../../../domain/consulta/workflow";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Card from "../../../../components/ui/Card";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import DateTimeField from "../../../../components/form/DateTimeField/DateTimeField";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../../../../components/ui/HighlightButton/HighlightButton";

import "../base.css";
import "./styles.css";

type ConsultaFormModel = {
  pacienteId: number | null;
  medicoId: number | null;
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
const duracaoOptions: readonly SelectOption<DuracaoMinutos>[] = DURACOES.map((d) => ({
  value: d,
  label: `${d} min`,
}));

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
    const parsed = Number(normalized);
    return Number.isFinite(parsed) ? parsed : 0;
  }

  const parsed = Number(cleaned);
  return Number.isFinite(parsed) ? parsed : 0;
}

function formatMoneyBR(value: number): string {
  if (!Number.isFinite(value)) return "";
  return value.toFixed(2).replace(".", ",");
}

function toFormModel(details: ConsultaDetailsViewModel): ConsultaFormModel {
  return {
    pacienteId: details.pacienteId,
    medicoId: details.medicoId,
    medicoNome: details.medicoNome,
    dataHora: normalizeDateTimeLocal(details.dataHora),
    duracaoMinutos: (details.duracaoMinutos as DuracaoMinutos) || 30,
    tipo: details.tipo,
    motivo: details.motivo === "—" ? "" : details.motivo,
    valorConsulta: formatMoneyBR(details.valorConsulta),
    meioPagamento: details.meioPagamento ?? "PIX",
    pago: details.pago,
    dataPagamento: normalizeDateTimeLocal(details.dataPagamento ?? ""),
  };
}

type FormSectionProps = {
  consultaId: number;
  initialDetails: ConsultaDetailsViewModel;
};

function ConsultaEditFormSection({ consultaId, initialDetails }: FormSectionProps) {
  const navigate = useNavigate();
  const metadataQuery = useConsultaMetadataQuery();
  const pacientesQuery = usePacientesQuery({ size: 200, sort: "primeiroNome,asc" });
  const updateMutation = useUpdateConsultaMutation();
  const [form, setForm] = useState<ConsultaFormModel>(() => toFormModel(initialDetails));

  const pacienteOptions = useMemo<readonly SelectOption<number>[]>(() => {
    return pacientesQuery.data.content.map((paciente) => ({
      value: paciente.id,
      label: paciente.nome,
    }));
  }, [pacientesQuery.data.content]);

  const tipoOptions = useMemo<readonly SelectOption<TipoConsulta>[]>(() => {
    const options = metadataQuery.data?.tipos ?? [];
    return options
      .filter((option): option is { code: TipoConsulta; label: string } => (
        option.code === "PRESENCIAL" || option.code === "TELECONSULTA" || option.code === "RETORNO"
      ))
      .map((option) => ({ value: option.code, label: option.label }));
  }, [metadataQuery.data]);

  const pagamentoOptions = useMemo<readonly SelectOption<MeioPagamento>[]>(() => {
    const options = metadataQuery.data?.meiosPagamento ?? [];
    return options
      .filter((option): option is { code: MeioPagamento; label: string } => (
        option.code === "PIX" || option.code === "CARTAO" || option.code === "DINHEIRO"
      ))
      .map((option) => ({ value: option.code, label: option.label }));
  }, [metadataQuery.data]);

  async function save(current: ConsultaFormModel) {
    const payload: ConsultaUpdatePayload = {
      pacienteId: current.pacienteId ?? undefined,
      medicoId: current.medicoId ?? undefined,
      dataHora: normalizeDateTimeLocal(current.dataHora),
      duracaoMinutos: current.duracaoMinutos,
      tipo: current.tipo,
      motivo: current.motivo.trim(),
      valorConsulta: parseMoneyToNumber(current.valorConsulta),
      meioPagamento: current.meioPagamento,
      pago: current.pago,
      dataPagamento: current.pago ? normalizeDateTimeLocal(current.dataPagamento || current.dataHora) : null,
      retorno: current.tipo === "RETORNO",
      dataLimiteRetorno: null,
      teleconsulta: current.tipo === "TELECONSULTA",
      linkAcesso: null,
      planoSaude: null,
      numeroCarteirinha: null,
    };

    const updated = await updateMutation.mutateAsync({ id: consultaId, payload });
    if (updated) {
      navigate(`/consultas/${updated.id}`);
    }
  }

  if (metadataQuery.isLoading || pacientesQuery.isLoading) {
    return (
      <div className="consultas-page consultas-page--form">
        <PageHeader title="Editar consulta" subtitle="Carregando dados" />
      </div>
    );
  }

  const error = updateMutation.error ?? pacientesQuery.error ?? metadataQuery.error;
  const canSave = Boolean(form.pacienteId && form.motivo.trim() && !updateMutation.isPending);

  if (!canEditConsulta(initialDetails.status)) {
    return (
      <div className="consultas-page consultas-page--form">
        <PageHeader
          title="Editar consulta"
          subtitle="Esta consulta não pode mais ser editada no status atual."
        />
      </div>
    );
  }

  return (
    <>
      <PageHeader title="Editar consulta" />

      <div className="mf-page-content">
        <Card>
          <div className="consultas-formWrap">
            <div className="consultas-form">
              {error ? <div className="mf-muted">{error}</div> : null}

              <div className="consultas-row2">
                <div className="consultas-field">
                  <span className="consultas-label">Paciente</span>
                  <SelectField<number>
                    value={form.pacienteId}
                    onChange={(pacienteId) => setForm((current) => ({ ...current, pacienteId }))}
                    options={pacienteOptions}
                    ariaLabel="Selecionar paciente"
                    disabled={pacienteOptions.length === 0}
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
                    onChange={(dataHora) => setForm((current) => ({ ...current, dataHora: normalizeDateTimeLocal(dataHora) }))}
                    step={60}
                    aria-label="Data e hora"
                  />
                </div>

                <div className="consultas-field">
                  <span className="consultas-label">Duração</span>
                  <SelectField<DuracaoMinutos>
                    value={form.duracaoMinutos}
                    onChange={(duracaoMinutos) => setForm((current) => ({ ...current, duracaoMinutos }))}
                    options={duracaoOptions}
                    ariaLabel="Selecionar duração"
                  />
                </div>

                <div className="consultas-field">
                  <span className="consultas-label">Tipo</span>
                  <SelectField<TipoConsulta>
                    value={form.tipo}
                    onChange={(tipo) => setForm((current) => ({ ...current, tipo }))}
                    options={tipoOptions}
                    ariaLabel="Selecionar tipo"
                    disabled={tipoOptions.length === 0}
                  />
                </div>
              </div>

              <div className="consultas-field">
                <span className="consultas-label">Motivo</span>
                <textarea
                  className="consultas-textarea"
                  value={form.motivo}
                  onChange={(e) => setForm((current) => ({ ...current, motivo: e.target.value }))}
                  placeholder="Descreva a queixa principal..."
                />
              </div>

              <div className="consultas-divider" />

              <div className="consultas-rowPay">
                <div className="consultas-field">
                  <span className="consultas-label">Valor (R$)</span>
                  <Input
                    value={form.valorConsulta}
                    onChange={(e) => setForm((current) => ({ ...current, valorConsulta: e.target.value }))}
                    placeholder="150,00"
                  />
                </div>

                <div className="consultas-field">
                  <span className="consultas-label">Meio de pagamento</span>
                  <SelectField<MeioPagamento>
                    value={form.meioPagamento}
                    onChange={(meioPagamento) => setForm((current) => ({ ...current, meioPagamento }))}
                    options={pagamentoOptions}
                    ariaLabel="Selecionar meio de pagamento"
                    disabled={pagamentoOptions.length === 0}
                  />
                </div>

                <div className="consultas-fieldInline">
                  <label className="consultas-checkbox">
                    <input
                      type="checkbox"
                      checked={form.pago}
                      onChange={(e) =>
                        setForm((current) => ({
                          ...current,
                          pago: e.target.checked,
                          dataPagamento: e.target.checked ? (current.dataPagamento || current.dataHora) : "",
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
                    value={form.dataPagamento}
                    onChange={(dataPagamento) =>
                      setForm((current) => ({ ...current, dataPagamento: normalizeDateTimeLocal(dataPagamento) }))
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

                <HighlightButton onClick={() => void save(form)} disabled={!canSave}>
                  {updateMutation.isPending ? "Salvando..." : "Salvar"}
                </HighlightButton>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </>
  );
}

export default function ConsultaEdit() {
  const { id } = useParams();
  const consultaId = useMemo(() => {
    const parsed = Number(id);
    return Number.isFinite(parsed) ? parsed : null;
  }, [id]);
  const detailsQuery = useConsultaDetailsQuery(consultaId);

  if (consultaId === null) {
    return (
      <div className="consultas-page consultas-page--form">
        <PageHeader title="Editar consulta" subtitle="Identificador inválido" />
      </div>
    );
  }

  if (detailsQuery.isLoading) {
    return (
      <div className="consultas-page consultas-page--form">
        <PageHeader title="Editar consulta" subtitle="Carregando dados" />
      </div>
    );
  }

  if (!detailsQuery.data) {
    return (
      <div className="consultas-page consultas-page--form">
        <PageHeader title="Editar consulta" subtitle={detailsQuery.error ?? "Consulta não encontrada"} />
      </div>
    );
  }

  return <ConsultaEditFormSection key={detailsQuery.data.id} consultaId={consultaId} initialDetails={detailsQuery.data} />;
}
