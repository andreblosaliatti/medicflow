import type { SelectOption } from "../form/SelectField/SelectField";
import ConsultaFormDrawer, { type ConsultaDraft } from "./AppointmentDetailDrawer";

type Props = {
  open: boolean;
  mode: "create" | "edit";
  drawerKey: string;
  value: ConsultaDraft | null;
  doctorId: number | null;
  doctorName: string;
  doctorOptions: readonly SelectOption<number>[];
  lockDoctor?: boolean;
  patientOptions: readonly SelectOption<number>[];
  lockPaciente?: boolean;
  isSaving?: boolean;
  onClose: () => void;
  onSave: (data: ConsultaDraft, mode: "create" | "edit") => void;
};

export default function ConsultaDrawerHost({
  open,
  mode,
  drawerKey,
  value,
  doctorId,
  doctorName,
  doctorOptions,
  lockDoctor,
  patientOptions,
  lockPaciente,
  isSaving,
  onClose,
  onSave,
}: Props) {
  return (
    <ConsultaFormDrawer
      key={drawerKey}
      open={open}
      mode={mode}
      initialValue={value}
      doctorId={doctorId}
      doctorName={doctorName}
      doctorOptions={doctorOptions}
      lockDoctor={lockDoctor}
      patientOptions={patientOptions}
      lockPaciente={lockPaciente}
      isSaving={isSaving}
      onClose={onClose}
      onSave={(data) => onSave(data, mode)}
    />
  );
}
