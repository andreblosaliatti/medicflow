import type { StatusConsulta, TipoConsulta } from "../../../domain/enums/statusConsulta";

export type AppointmentEvent = {
  id: string;
  patientId?: number | null;
  patientName: string;
  professionalId?: number | null;
  professionalName: string;
  type: TipoConsulta;
  status: StatusConsulta;
  start: Date;
  end: Date;
  notes?: string;
  room?: string;
  phone?: string;
  linkAcesso?: string | null;
};
