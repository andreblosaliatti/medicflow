type Variant = "success" | "warning" | "danger";

export default function Badge({ variant, children }: { variant: Variant; children: string }) {
  return <span className={`badge ${variant}`}>{children}</span>;
}
