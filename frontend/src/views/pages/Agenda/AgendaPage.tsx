import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import {
  useAgendaEventsQuery,
  useCreateConsultaMutation,
  useUpdateConsultaMutation,
} from "../../../api/consultas/hooks";
import { usePacientesQuery } from "../../../api/pacientes/hooks";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";
import Input from "../../../components/form/Input";
import CalendarView from "../../../components/Agenda/CalendarView";
import SelectField, { type SelectOption } from "../../../components/form/SelectField/SelectField";
import { useAuth } from "../../../auth/useAuth";
import type { StatusConsulta, TipoConsulta } from "../../../domain/enums/statusConsulta";
import ConsultaDrawerHost from "../../../components/Agenda/ConsultaDrawerHost";
import { useConsultaDrawerController } from "../../../components/Agenda/useConsultaDrawerController";
import type { ConsultaDraft } from "../../../components/Agenda/AppointmentDetailDrawer";
import type { AppointmentEvent } from "./types";

import "./styles.css";

type StatusFilter = StatusConsulta | "TODOS";
type TypeFilter = TipoConsulta | "TODOS";

type VisibleRange = {
  start: Date;
  end: Date;
};

type NovaConsultaState = {
  from?: string;
  novaConsulta?: { pacienteId: number; pacienteNome: string };
};

const statusOptions: readonly SelectOption<StatusFilter>[] = [
  { value: "TODOS", label: "Todos" },
  { value: "AGENDADA", label: "Agendada" },
  { value: "CONFIRMADA", label: "Confirmada" },
  { value: "EM_ATENDIMENTO", label: "Em atendimento" },
  { value: "CONCLUIDA", label: "Concluída" },
  { value: "CANCELADA", label: "Cancelada" },
] as const;

const typeOptions: readonly SelectOption<TypeFilter>[] = [
  { value: "TODOS", label: "Todos" },
  { value: "PRESENCIAL", label: "Presencial" },
  { value: "TELECONSULTA", label: "Teleconsulta" },
  { value: "RETORNO", label: "Retorno" },
] as const;

function startOfWeek(date: Date) {
  const result = new Date(date);
  const day = result.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  result.setDate(result.getDate() + diff);
  result.setHours(0, 0, 0, 0);
  return result;
}

function endOfWeek(date: Date) {
  const result = startOfWeek(date);
  result.setDate(result.getDate() + 6);
  result.setHours(23, 59, 59, 999);
  return result;
}

function toIsoLocal(d: Date) {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const hh = String(d.getHours()).padStart(2, "0");
  const mi = String(d.getMinutes()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
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


function toLocalDateTimeParam(date: Date) {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mi = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}:${ss}`;
}

function normalizeDateTimeLocal(value: string): string {
  if (!value) return "";
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) return value;
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value)) return value.slice(0, 16);
  return value;
}


function snapDuration(minutes: number): 15 | 30 | 45 | 60 {
  if (minutes <= 15) return 15;
  if (minutes <= 30) return 30;
  if (minutes <= 45) return 45;
  return 60;
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

function fromEventToDraft(event: AppointmentEvent, doctorId: number, doctorName: string): ConsultaDraft {
  return {
    ...emptyDraft(doctorId, doctorName),
    pacienteId: event.patientId ?? null,
    pacienteNome: event.patientName,
    medicoId: event.professionalId ?? doctorId,
    medicoNome: event.professionalName || doctorName,
    dataHora: toIsoLocal(event.start),
    duracaoMinutos: snapDuration(Math.round((event.end.getTime() - event.start.getTime()) / 60000)),
    tipo: event.type,
    status: event.status,
    motivo: event.notes ?? "",
    teleconsulta: event.type === "TELECONSULTA",
    linkAcesso: event.linkAcesso ?? "",
    retorno: event.type === "RETORNO",
    dataLimiteRetorno: "",
  };
}

export default function AgendaPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const doctorId = Number(user?.id ?? 0);
  const doctorName = user?.name ?? "Profissional";

  const [range, setRange] = useState<VisibleRange>(() => ({
    start: startOfWeek(new Date()),
    end: endOfWeek(new Date()),
  }));
  const [status, setStatus] = useState<StatusFilter>("TODOS");
  const [type, setType] = useState<TypeFilter>("TODOS");
  const [search, setSearch] = useState("");
  const [lockPaciente, setLockPaciente] = useState(false);

  const pacientesQuery = usePacientesQuery({ size: 200, sort: "primeiroNome,asc" });
  const agendaQuery = useAgendaEventsQuery({
    dataHoraInicio: toLocalDateTimeParam(range.start),
    dataHoraFim: toLocalDateTimeParam(range.end),
    size: 500,
    sort: "dataHora,asc",
  });
  const createMutation = useCreateConsultaMutation();
  const updateMutation = useUpdateConsultaMutation();

  const patientOptions = useMemo<readonly SelectOption<number>[]>(() => {
    return pacientesQuery.data.content.map((paciente) => ({
      value: paciente.id,
      label: paciente.nome,
    }));
  }, [pacientesQuery.data.content]);

  const drawer = useConsultaDrawerController({
    doctorId,
    doctorName,
    emptyDraft,
    fromEventToDraft,
  });

  useEffect(() => {
    const state = (location.state ?? {}) as NovaConsultaState;
    const novaConsulta = state.novaConsulta;
    if (!novaConsulta) return;

    const timer = window.setTimeout(() => {
      setLockPaciente(true);
      drawer.openCreateForPaciente(novaConsulta.pacienteId, novaConsulta.pacienteNome);
      navigate(location.pathname, { replace: true, state: { ...state, novaConsulta: undefined } });
    }, 0);

    return () => window.clearTimeout(timer);
  }, [drawer, location.pathname, location.state, navigate]);

  const events = useMemo(() => {
    return agendaQuery.data.filter((event) => {
      if (status !== "TODOS" && event.status !== status) return false;
      if (type !== "TODOS" && event.type !== type) return false;

      if (search.trim()) {
        const q = search.trim().toLowerCase();
        const haystack = `${event.patientName} ${event.professionalName} ${event.type} ${event.status} ${event.notes ?? ""}`.toLowerCase();
        if (!haystack.includes(q)) return false;
      }

      return true;
    });
  }, [agendaQuery.data, search, status, type]);

  const error = agendaQuery.error ?? pacientesQuery.error ?? createMutation.error ?? updateMutation.error;
  const isSaving = createMutation.isPending || updateMutation.isPending;

  async function handleSave(data: ConsultaDraft, mode: "create" | "edit") {
    if (!data.pacienteId) return;

    const payload = {
      pacienteId: data.pacienteId,
      medicoId: data.medicoId,
      dataHora: normalizeDateTimeLocal(data.dataHora),
      duracaoMinutos: data.duracaoMinutos,
      tipo: data.tipo,
      status: mode === "create" ? "AGENDADA" : data.status,
      motivo: data.motivo.trim(),
      valorConsulta: parseMoneyToNumber(data.valorConsulta),
      meioPagamento: data.meioPagamento,
      pago: data.pago,
      dataPagamento: data.pago ? normalizeDateTimeLocal(data.dataPagamento || data.dataHora) : null,
      retorno: data.tipo === "RETORNO",
      dataLimiteRetorno: data.tipo === "RETORNO" && data.dataLimiteRetorno ? normalizeDateTimeLocal(data.dataLimiteRetorno) : null,
      teleconsulta: data.tipo === "TELECONSULTA",
      linkAcesso: data.tipo === "TELECONSULTA" ? data.linkAcesso || null : null,
      planoSaude: null,
      numeroCarteirinha: null,
      anamnese: null,
      exameFisico: null,
      diagnostico: null,
      prescricao: null,
      observacoes: null,
    };

    if (mode === "create") {
      const created = await createMutation.mutateAsync(payload);
      if (created) {
        setLockPaciente(false);
        drawer.close();
        void agendaQuery.refetch();
      }
      return;
    }

    const editingId = drawer.editingEventId ? Number(drawer.editingEventId) : null;
    if (!editingId) return;

    const updated = await updateMutation.mutateAsync({ id: editingId, payload });
    if (updated) {
      setLockPaciente(false);
      drawer.close();
      void agendaQuery.refetch();
    }
  }

  return (
    <div className="agenda-page">
      <PageHeader
        title="Agenda"
        actions={[
          {
            label: "Novo agendamento",
            icon: "➕",
            variant: "primary",
            onClick: () => {
              setLockPaciente(false);
              drawer.openCreate();
            },
          },
        ]}
      />

      <div className="mf-page-content">
        <Card>
          <div className="agenda-sectionTitle">Filtros</div>

          <div className="agenda-filters">
            <SelectField<StatusFilter>
              value={status}
              onChange={setStatus}
              options={statusOptions}
              placeholder="Status"
              ariaLabel="Filtro por status"
            />

            <SelectField<TypeFilter>
              value={type}
              onChange={setType}
              options={typeOptions}
              placeholder="Tipo"
              ariaLabel="Filtro por tipo"
            />

            <PrimaryButton
              onClick={() => {
                setStatus("TODOS");
                setType("TODOS");
                setSearch("");
              }}
            >
              Limpar
            </PrimaryButton>
          </div>

          <div className="agenda-searchRow">
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Buscar por paciente, médico, observação..."
            />
          </div>

          {error ? <div className="mf-muted">{error}</div> : null}

          <div className="agenda-legend">
            <span className="agenda-dot agenda-dotConfirmed" />
            <span className="mf-muted">Confirmada</span>

            <span className="agenda-dot agenda-dotPending" />
            <span className="mf-muted">Agendada</span>

            <span className="agenda-dot agenda-dotCanceled" />
            <span className="mf-muted">Cancelada</span>
          </div>
        </Card>

        <Card className="agenda-calendarCard">
          <div className="agenda-sectionTitle">Calendário</div>

          <div className="agenda-calendarWrap">
            <CalendarView
              events={events}
              onSelectEvent={drawer.openEditFromEvent}
              onRangeChange={setRange}
            />
          </div>
        </Card>
      </div>

      <ConsultaDrawerHost
        open={drawer.open}
        mode={drawer.mode}
        drawerKey={drawer.drawerKey}
        value={drawer.value}
        doctorId={doctorId}
        doctorName={doctorName}
        patientOptions={patientOptions}
        lockPaciente={lockPaciente}
        isSaving={isSaving}
        onClose={() => {
          setLockPaciente(false);
          drawer.close();
        }}
        onSave={(data, mode) => void handleSave(data, mode)}
      />
    </div>
  );
}
