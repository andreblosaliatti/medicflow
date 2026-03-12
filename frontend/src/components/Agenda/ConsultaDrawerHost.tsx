import ConsultaFormDrawer, { type ConsultaDraft } from "./AppointmentDetailDrawer";

type Props = {
  open: boolean;
  mode: "create" | "edit";
  drawerKey: string;

  value: ConsultaDraft | null;

  doctorId: string;
  doctorName: string;

  // ✅ ADD (opcional)
  lockPaciente?: boolean;

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
  lockPaciente,
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
      lockPaciente={lockPaciente}
      onClose={onClose}
      onSave={(data) => onSave(data, mode)}
    />
  );
}