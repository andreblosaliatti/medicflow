import { useMemo, useState } from "react";
import type { AppointmentEvent } from "../../views/pages/Agenda/types";
import type { ConsultaDraft } from "./AppointmentDetailDrawer";

type Mode = "create" | "edit";

type Params = {
  doctorId: number | null;
  doctorName: string;
  emptyDraft: (doctorId: number | null, doctorName: string) => ConsultaDraft;
  fromEventToDraft: (event: AppointmentEvent, doctorId: number | null, doctorName: string) => ConsultaDraft;
};

type Controller = {
  open: boolean;
  mode: Mode;
  value: ConsultaDraft | null;
  editingEventId: string | null;
  drawerKey: string;
  openCreate: () => void;
  openCreateForPaciente: (pacienteId: number, pacienteNome: string) => void;
  openEditFromEvent: (event: AppointmentEvent) => void;
  close: () => void;
  setValue: (value: ConsultaDraft) => void;
};

export function useConsultaDrawerController(params: Params): Controller {
  const { doctorId, doctorName, emptyDraft, fromEventToDraft } = params;

  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState<Mode>("create");
  const [value, setValue] = useState<ConsultaDraft | null>(null);
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

  function openEditFromEvent(event: AppointmentEvent) {
    setMode("edit");
    setEditingEventId(event.id);
    setValue(fromEventToDraft(event, doctorId, doctorName));
    setOpen(true);
  }

  function openCreateForPaciente(pacienteId: number, pacienteNome: string) {
    setMode("create");
    setEditingEventId(null);

    const draft = emptyDraft(doctorId, doctorName);
    draft.pacienteId = pacienteId;
    draft.pacienteNome = pacienteNome;

    setValue(draft);
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
    openCreateForPaciente,
    openEditFromEvent,
    close,
    setValue,
  };
}
