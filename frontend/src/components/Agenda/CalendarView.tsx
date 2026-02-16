import { useCallback, useMemo, useState } from "react";
import { Calendar, dateFnsLocalizer, Views, type View } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import { ptBR } from "date-fns/locale";

import type { AppointmentEvent } from "../../views/pages/Agenda/Types";
import styles from "./calendar-view.module.css";

const locales = { "pt-BR": ptBR };

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { locale: ptBR }),
  getDay,
  locales,
});

type Props = {
  events: AppointmentEvent[];
  onSelectEvent: (ev: AppointmentEvent) => void;
};

export default function CalendarView({ events, onSelectEvent }: Props) {
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
    []
  );

  const formats = useMemo(
    () => ({
      dayHeaderFormat: (d: Date) => format(d, "EEEE, dd/MM", { locale: ptBR }),
      weekdayFormat: (d: Date) => format(d, "EEE", { locale: ptBR }),
      dayRangeHeaderFormat: ({ start, end }: { start: Date; end: Date }) =>
        `${format(start, "dd/MM", { locale: ptBR })} — ${format(end, "dd/MM", { locale: ptBR })}`,
      timeGutterFormat: (d: Date) => format(d, "HH:mm", { locale: ptBR }),
      eventTimeRangeFormat: ({ start, end }: { start: Date; end: Date }) =>
        `${format(start, "HH:mm", { locale: ptBR })}–${format(end, "HH:mm", { locale: ptBR })}`,
      agendaDateFormat: (d: Date) => format(d, "dd/MM/yyyy", { locale: ptBR }),
      agendaTimeFormat: (d: Date) => format(d, "HH:mm", { locale: ptBR }),
      agendaHeaderFormat: ({ start, end }: { start: Date; end: Date }) =>
        `${format(start, "dd/MM/yyyy", { locale: ptBR })} — ${format(end, "dd/MM/yyyy", { locale: ptBR })}`,
    }),
    []
  );

  const handleSelectEvent = useCallback(
    (ev: AppointmentEvent) => onSelectEvent(ev),
    [onSelectEvent]
  );

  const eventPropGetter = useCallback((event: AppointmentEvent) => {
    const status = event.status;

    const className =
      status === "CONFIRMADO"
        ? styles.evConfirmed
        : status === "CANCELADO"
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
        popup
        messages={messages}
        formats={formats}
        onSelectEvent={handleSelectEvent}
        eventPropGetter={eventPropGetter}
      />
    </div>
  );
}