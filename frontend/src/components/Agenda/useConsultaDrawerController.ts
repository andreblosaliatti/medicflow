import { useMemo, useState } from "react";
import type { AppointmentEvent } from "../../views/pages/Agenda/Types";
import type { ConsultaDraft } from "./AppointmentDetailDrawer";

type Mode = "create" | "edit";

type Params = {
  doctorId: string;
  doctorName: string;

  emptyDraft: (doctorId: string, doctorName: string) => ConsultaDraft;
  fromEventToDraft: (ev: AppointmentEvent, doctorId: string, doctorName: string) => ConsultaDraft;
};

type Controller = {
  open: boolean;
  mode: Mode;
  value: ConsultaDraft | null;
  editingEventId: string | null;
  drawerKey: string;

  openCreate: () => void;
  openEditFromEvent: (ev: AppointmentEvent) => void;
  close: () => void;

  setValue: (v: ConsultaDraft) => void;
};

export function useConsultaDrawerController(params: Params): Controller {
  const { doctorId, doctorName, emptyDraft, fromEventToDraft } = params;

  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState<Mode>("create");
  const [value, setValue] = useState<ConsultaDraft | null>(null);

  // ✅ guarda o id do evento quando estiver editando
  const [editingEventId, setEditingEventId] = useState<string | null>(null);

  function close() {
    setOpen(false);
  }

  function openCreate() {
    setMode("create");
    setEditingEventId(null);
    setValue(emptyDraft(doctorId, doctorName));
    setOpen(true);
  }

  function openEditFromEvent(ev: AppointmentEvent) {
    setMode("edit");
    setEditingEventId(ev.id);
    setValue(fromEventToDraft(ev, doctorId, doctorName));
    setOpen(true);
  }

  const drawerKey = useMemo(() => {
    const suffix = value?.dataHora ?? "x";
    return mode === "create"
      ? `create-${suffix}`
      : `edit-${editingEventId ?? "x"}-${suffix}`;
  }, [mode, editingEventId, value?.dataHora]);

  return {
    open,
    mode,
    value,
    editingEventId,
    drawerKey,
    openCreate,
    openEditFromEvent,
    close,
    setValue,
  };
}