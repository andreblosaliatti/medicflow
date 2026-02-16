type Props = {
  title: string;
  value: string | number;
  subtitle?: string;
  icon?: string; // emoji por enquanto
  variant?: "default" | "info" | "success" | "warning";
};

function getVariantClass(variant: Props["variant"]) {
  if (variant === "success") return "statcard--success";
  if (variant === "warning") return "statcard--warning";
  if (variant === "info") return "statcard--info";
  return "statcard--default";
}

export default function DashboardStatCard({
  title,
  value,
  subtitle,
  icon,
  variant = "default",
}: Props) {
  return (
    <div className={`statcard ${getVariantClass(variant)}`}>
      <div className="statcard-icon" aria-hidden="true">
        {icon ?? "•"}
      </div>

      <div className="statcard-body">
        <div className="statcard-title">{title}</div>
        <div className="statcard-value">{value}</div>
        {subtitle && <div className="statcard-subtitle">{subtitle}</div>}
      </div>
    </div>
  );
}
