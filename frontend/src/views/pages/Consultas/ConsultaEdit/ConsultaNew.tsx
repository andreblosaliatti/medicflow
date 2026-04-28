import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import { useCreateConsultaMutation, useConsultaMetadataQuery } from "../../../../api/consultas/hooks";
import type { ConsultaCreatePayload } from "../../../../api/consultas/types";
import { usePacientesQuery } from "../../../../api/pacientes/hooks";
import type { DuracaoMinutos, MeioPagamento, TipoConsulta } from "../../../../domain/enums/statusConsulta";
import { useAuth } from "../../../../context/useAuth";
import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import DateTimeField from "../../../../components/form/DateTimeField/DateTimeField";
import SecondaryButton from "../../../../components/ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../../../../components/ui/HighlightButton/HighlightButton";
import Panel from "../../../../components/ui/Panel/Panel";

import "../base.css";
import "./styles.css";

type ConsultaCreateForm = {
  pacienteId: number | null;
  medicoId: number;
  medicoNome: string;
  dataHora: string;
  duracaoMinutos: DuracaoMinutos;
  tipo: TipoConsulta;
  linkAcesso: string;
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

export default function ConsultaCreate() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const metadataQuery = useConsultaMetadataQuery();
  const pacientesQuery = usePacientesQuery({ size: 200, sort: "nome,asc" });
  const createMutation = useCreateConsultaMutation();

  const medicoId = Number(user?.id ?? 0);
  const medicoNome = user?.name ?? "Profissional";

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
        option.code === "PRESENCIAL"
        || option.code === "TELECONSULTA"
        || option.code === "RETORNO"
        || option.code === "URGENCIA"
      ))
      .map((option) => ({ value: option.code, label: option.label }));
  }, [metadataQuery.data]);

  const pagamentoOptions = useMemo<readonly SelectOption<MeioPagamento>[]>(() => {
    const options = metadataQuery.data?.meiosPagamento ?? [];
    return options
      .filter((option): option is { code: MeioPagamento; label: string } => (
        option.code === "DEBITO"
        || option.code === "CREDITO"
        || option.code === "PIX"
        || option.code === "DINHEIRO"
      ))
      .map((option) => ({ value: option.code, label: option.label }));
  }, [metadataQuery.data]);

  const [form, setForm] = useState<ConsultaCreateForm>(() => {
    const now = new Date();
    now.setMinutes(0, 0, 0);

    return {
      pacienteId: null,
      medicoId,
      medicoNome,
      dataHora: toIsoLocal(now),
      duracaoMinutos: 30,
      tipo: "PRESENCIAL",
      linkAcesso: "",
      motivo: "",
      valorConsulta: "",
      meioPagamento: "PIX",
      pago: false,
      dataPagamento: "",
    };
  });

  async function save() {
    if (!form.pacienteId) return;

    const payload: ConsultaCreatePayload = {
      dataHora: normalizeDateTimeLocal(form.dataHora),
      tipo: form.tipo,
      status: "AGENDADA",
      valorConsulta: parseMoneyToNumber(form.valorConsulta),
      meioPagamento: form.meioPagamento,
      pago: form.pago,
      dataPagamento: form.pago ? normalizeDateTimeLocal(form.dataPagamento || form.dataHora) : null,
      duracaoMinutos: form.duracaoMinutos,
      retorno: form.tipo === "RETORNO",
      dataLimiteRetorno: null,
      linkAcesso: form.tipo === "TELECONSULTA" ? form.linkAcesso.trim() : null,
      planoSaude: null,
      numeroCarteirinha: null,
      motivo: form.motivo.trim(),
      anamnese: null,
      exameFisico: null,
      diagnostico: null,
      prescricao: null,
      observacoes: null,
      pacienteId: form.pacienteId,
      medicoId: form.medicoId,
    };

    const created = await createMutation.mutateAsync(payload);
    if (created) {
      navigate(`/consultas/${created.id}`);
    }
  }

  const error = createMutation.error ?? pacientesQuery.error ?? metadataQuery.error;
  const isLoading = pacientesQuery.isLoading || metadataQuery.isLoading;
  const hasValidTeleconsultaLink = form.tipo !== "TELECONSULTA" || Boolean(form.linkAcesso.trim());
  const canSave = Boolean(form.pacienteId && form.motivo.trim() && hasValidTeleconsultaLink && !createMutation.isPending && !isLoading);

  return (
    <>
      <PageHeader title="Nova consulta" />

      <div className="mf-page-content">
        <Panel title="">
          <div className="consultas-formWrap">
            <div className="consultas-form">
              {error ? <div className="mf-muted">{error}</div> : null}

              <div className="consultas-row2">
                <div className="consultas-field">
                  <span className="consultas-label">Paciente</span>
                  <SelectField<number>
                    value={form.pacienteId}
                    onChange={(pacienteId) => setForm((s) => ({ ...s, pacienteId }))}
                    options={pacienteOptions}
                    placeholder={isLoading ? "Carregando pacientes..." : "Selecionar paciente"}
                    ariaLabel="Selecionar paciente"
                    disabled={isLoading || pacienteOptions.length === 0}
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
                    onChange={(tipo) => setForm((s) => ({
                      ...s,
                      tipo,
                      linkAcesso: tipo === "TELECONSULTA" ? s.linkAcesso : "",
                    }))}
                    options={tipoOptions}
                    ariaLabel="Selecionar tipo"
                    disabled={tipoOptions.length === 0}
                  />
                </div>
              </div>


              {form.tipo === "TELECONSULTA" ? (
                <div className="consultas-field">
                  <span className="consultas-label">Link de acesso</span>
                  <Input
                    value={form.linkAcesso}
                    onChange={(e) => setForm((s) => ({ ...s, linkAcesso: e.target.value }))}
                    placeholder="https://..."
                  />
                </div>
              ) : null}
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
                    disabled={pagamentoOptions.length === 0}
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
                <HighlightButton onClick={() => void save()} disabled={!canSave}>
                  {createMutation.isPending ? "Salvando..." : "Salvar"}
                </HighlightButton>
              </div>
            </div>
          </div>
        </Panel>
      </div>
    </>
  );
}
