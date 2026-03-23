import { useCallback, useMemo, useState } from "react";
import { Calendar, dateFnsLocalizer, Views, type View } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import { ptBR } from "date-fns/locale";

import type { AppointmentEvent } from "../../views/pages/Agenda/types";
import styles from "./calendar-view.module.css";

const locales = { "pt-BR": ptBR };

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { locale: ptBR }),
  getDay,
  locales,
});

type CalendarRange = {
  start: Date;
  end: Date;
};

type Props = {
  events: AppointmentEvent[];
  onSelectEvent: (event: AppointmentEvent) => void;
  onRangeChange?: (range: CalendarRange) => void;
};

function toRange(value: Date[] | { start: Date; end: Date } | Date): CalendarRange | null {
  if (Array.isArray(value)) {
    if (value.length === 0) return null;
    return { start: value[0], end: value[value.length - 1] };
  }

  if (value instanceof Date) {
    return { start: value, end: value };
  }

  if (value && "start" in value && "end" in value) {
    return { start: value.start, end: value.end };
  }

  return null;
}

export default function CalendarView({ events, onSelectEvent, onRangeChange }: Props) {
  const [view, setView] = useState<View>(Views.WEEK);
  const [date, setDate] = useState<Date>(new Date());

  const messages = useMemo(
    () => ({
      next: "Próximo",
      previous: "Anterior",
      today: "Hoje",
      month: "Mês",
      week: "Semana",
      day: "Dia",
      agenda: "Lista",
      date: "Data",
      time: "Hora",
      event: "Evento",
      noEventsInRange: "Sem eventos neste período",
      showMore: (total: number) => `+${total} mais`,
    }),
    [],
  );

  const formats = useMemo(
    () => ({
      dayHeaderFormat: (value: Date) => format(value, "EEEE, dd/MM", { locale: ptBR }),
      weekdayFormat: (value: Date) => format(value, "EEE", { locale: ptBR }),
      dayRangeHeaderFormat: ({ start, end }: { start: Date; end: Date }) =>
        `${format(start, "dd/MM", { locale: ptBR })} — ${format(end, "dd/MM", { locale: ptBR })}`,
      timeGutterFormat: (value: Date) => format(value, "HH:mm", { locale: ptBR }),
      eventTimeRangeFormat: ({ start, end }: { start: Date; end: Date }) =>
        `${format(start, "HH:mm", { locale: ptBR })}–${format(end, "HH:mm", { locale: ptBR })}`,
      agendaDateFormat: (value: Date) => format(value, "dd/MM/yyyy", { locale: ptBR }),
      agendaTimeFormat: (value: Date) => format(value, "HH:mm", { locale: ptBR }),
      agendaHeaderFormat: ({ start, end }: { start: Date; end: Date }) =>
        `${format(start, "dd/MM/yyyy", { locale: ptBR })} — ${format(end, "dd/MM/yyyy", { locale: ptBR })}`,
    }),
    [],
  );

  const handleSelectEvent = useCallback((event: AppointmentEvent) => onSelectEvent(event), [onSelectEvent]);

  const handleRangeChange = useCallback(
    (range: Date[] | { start: Date; end: Date } | Date) => {
      const normalized = toRange(range);
      if (normalized && onRangeChange) {
        onRangeChange(normalized);
      }
    },
    [onRangeChange],
  );

  const eventPropGetter = useCallback((event: AppointmentEvent) => {
    const status = event.status;

    const className =
      status === "CONFIRMADA"
        ? styles.evConfirmed
        : status === "CANCELADA"
          ? styles.evCanceled
          : styles.evPending;

    return { className };
  }, []);

  return (
    <div className={styles.wrap}>
      <Calendar<AppointmentEvent>
        localizer={localizer}
        culture="pt-BR"
        events={events}
        startAccessor="start"
        endAccessor="end"
        views={[Views.MONTH, Views.WEEK, Views.DAY, Views.AGENDA]}
        view={view}
        onView={setView}
        date={date}
        onNavigate={setDate}
        onRangeChange={handleRangeChange}
        popup
        messages={messages}
        formats={formats}
        onSelectEvent={handleSelectEvent}
        eventPropGetter={eventPropGetter}
      />
    </div>
  );
}
