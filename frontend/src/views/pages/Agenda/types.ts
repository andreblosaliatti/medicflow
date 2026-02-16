import type { StatusConsulta, TipoConsulta } from "../../../domain/enums/statusConsulta";

export type AppointmentEvent = {
  id: string;
  patientName: string;
  professionalName: string;

  type: TipoConsulta;
  status: StatusConsulta;

  start: Date;
  end: Date;

  notes?: string;
  room?: string;
  phone?: string;
};