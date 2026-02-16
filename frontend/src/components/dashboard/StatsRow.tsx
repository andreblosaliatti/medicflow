import DashboardStatCard from "./DashBoardStatCard";

type Stat = {
  title: string;
  value: string;
  subtitle?: string;
  icon?: string;
  variant?: "default" | "info" | "success" | "warning";
};

type Props = {
  items: Stat[];
};

export default function StatsRow({ items }: Props) {
  return (
    <section className="dash-stats">
      {items.map((it, idx) => (
        <DashboardStatCard
          key={`${it.title}-${idx}`}
          title={it.title}
          value={it.value}
          subtitle={it.subtitle}
          icon={it.icon}
          variant={it.variant}
        />
      ))}
    </section>
  );
}
