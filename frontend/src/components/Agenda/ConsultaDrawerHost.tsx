import ConsultaFormDrawer, { type ConsultaDraft } from "./AppointmentDetailDrawer";

export default function ConsultaDrawerHost({
  open,
  mode,
  drawerKey,
  value,
  doctorId,
  doctorName,
  onClose,
  onSave,
}: {
  open: boolean;
  mode: "create" | "edit";
  drawerKey: string;
  value: ConsultaDraft | null;
  doctorId: string;
  doctorName: string;
  onClose: () => void;
  onSave: (data: ConsultaDraft, mode: "create" | "edit") => void;
}) {
  return (
    <ConsultaFormDrawer
      key={drawerKey}
      open={open}
      mode={mode}
      initialValue={value}
      doctorId={doctorId}
      doctorName={doctorName}
      onClose={onClose}
      onSave={(data) => onSave(data, mode)}
    />
  );
}