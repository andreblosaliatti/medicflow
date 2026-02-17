import { useMemo, useState } from "react";

import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";
import Input from "../../../components/form/Input";

import CalendarView from "../../../components/Agenda/CalendarView";
import SelectField, { type SelectOption } from "../../../components/form/SelectField/SelectField";

import { getSessionUser } from "../../../auth/session";

import type { AppointmentEvent } from "./Types";
import type { StatusConsulta, TipoConsulta } from "../../../domain/enums/statusConsulta";

import { toAppointmentEvents } from "../../../mocks/mappers";

import styles from "./agenda-page.module.css";

import ConsultaDrawerHost from "../../../components/Agenda/ConsultaDrawerHost";
import { useConsultaDrawerController } from "../../../components/Agenda/useConsultaDrawerController";
import type { ConsultaDraft } from "../../../components/Agenda/AppointmentDetailDrawer";

type StatusFilter = StatusConsulta | "TODOS";
type TypeFilter = TipoConsulta | "TODOS";

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

type DuracaoMinutos = 15 | 30 | 45 | 60;

function snapDuration(mins: number): DuracaoMinutos {
  if (mins <= 15) return 15;
  if (mins <= 30) return 30;
  if (mins <= 45) return 45;
  return 60;
}

function toIsoLocal(d: Date) {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const hh = String(d.getHours()).padStart(2, "0");
  const mi = String(d.getMinutes()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
}

function minutesBetween(start: Date, end: Date): DuracaoMinutos {
  const mins = Math.max(10, Math.round((end.getTime() - start.getTime()) / 60000));
  return snapDuration(mins);
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

function fromEventToDraft(ev: AppointmentEvent, doctorId: string, doctorName: string): ConsultaDraft {
  return {
    ...emptyDraft(doctorId, doctorName),

    pacienteNome: ev.patientName,
    medicoNome: ev.professionalName,

    dataHora: toIsoLocal(ev.start),
    duracaoMinutos: minutesBetween(ev.start, ev.end),

    tipo: ev.type,
    status: ev.status,

    motivo: ev.notes ?? "",
    teleconsulta: ev.type === "TELECONSULTA",
    retorno: ev.type === "RETORNO",
  };
}

function draftToEvent(d: ConsultaDraft, id?: string): AppointmentEvent {
  const start = new Date(d.dataHora);
  const end = new Date(start.getTime() + d.duracaoMinutos * 60000);

  return {
    id: id ?? String(Date.now()),
    patientName: d.pacienteNome || "Novo paciente",
    professionalName: d.medicoNome || "Dr. João Carvalho",

    type: d.tipo,
    status: d.status,

    start,
    end,

    room: d.tipo === "TELECONSULTA" ? "Teleconsulta" : "Sala 01",
    phone: "",
    notes: d.motivo,
  };
}

export default function AgendaPage() {
  const user = getSessionUser();
  const doctorId = String(user.id ?? "1");
  const doctorName = user.name ?? "Dr. João Carvalho";

  // ✅ Fonte única: seed/mocks centralizados (mappers)
  const [allEvents, setAllEvents] = useState<AppointmentEvent[]>(() => toAppointmentEvents());

  const [status, setStatus] = useState<StatusFilter>("TODOS");
  const [type, setType] = useState<TypeFilter>("TODOS");
  const [search, setSearch] = useState("");

  // ✅ Controller do Drawer (reutilizável em qualquer página)
  const drawer = useConsultaDrawerController({
    doctorId,
    doctorName,
    emptyDraft,
    fromEventToDraft,
  });

  const events = useMemo(() => {
    return allEvents.filter((e) => {
      if (status !== "TODOS" && e.status !== status) return false;
      if (type !== "TODOS" && e.type !== type) return false;

      if (search.trim()) {
        const q = search.trim().toLowerCase();
        const hay = `${e.patientName} ${e.professionalName} ${e.type} ${e.status} ${e.notes ?? ""}`.toLowerCase();
        if (!hay.includes(q)) return false;
      }

      return true;
    });
  }, [allEvents, status, type, search]);

  return (
    <div className={styles.page}>
      <PageHeader
        title="Agenda"
        actions={[
          {
            label: "Novo agendamento",
            icon: "➕",
            variant: "primary",
            onClick: drawer.openCreate,
          },
        ]}
      />

      <div className={styles.grid}>
        <Card>
          <div className={styles.sectionTitle}>Filtros</div>

          <div className={styles.filters}>
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

          <div className={styles.searchRow}>
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Buscar por paciente, médico, observação..."
            />
          </div>

          {/* Legenda (simplificada) */}
          <div className={styles.legend}>
            <span className={`${styles.dot} ${styles.dotConfirmed}`} />
            <span className="mf-muted">Confirmada</span>

            <span className={`${styles.dot} ${styles.dotPending}`} />
            <span className="mf-muted">Agendada</span>

            <span className={`${styles.dot} ${styles.dotCanceled}`} />
            <span className="mf-muted">Cancelada</span>
          </div>
        </Card>

        <Card className={styles.calendarCard}>
          <div className={styles.sectionTitle}>Calendário</div>

          <div className={styles.calendarWrap}>
            <CalendarView events={events} onSelectEvent={drawer.openEditFromEvent} />
          </div>
        </Card>
      </div>

      {/* ✅ Drawer único, sempre com o mesmo comportamento */}
      <ConsultaDrawerHost
        open={drawer.open}
        mode={drawer.mode}
        drawerKey={drawer.drawerKey}
        value={drawer.value}
        doctorId={doctorId}
        doctorName={doctorName}
        onClose={drawer.close}
        onSave={(data, mode) => {
          if (mode === "create") {
            const newEv = draftToEvent(data);
            setAllEvents((prev) => [newEv, ...prev]);
            drawer.close();
            return;
          }

          // ✅ EDIT REAL: atualiza pelo id do evento selecionado
          const id = drawer.editingEventId;
          if (!id) {
            // sem id não existe edit (estado inconsistente)
            drawer.close();
            return;
          }

          const updated = draftToEvent(data, id);
          setAllEvents((prev) => prev.map((ev) => (ev.id === id ? updated : ev)));
          drawer.close();
        }}
      />
    </div>
  );
}