type Tone = "blue" | "green" | "amber" | "gray";

type Props = {
  title: string;
  value: string | number;
  subtitle?: string;
  icon?: string; // por enquanto emoji
  tone?: Tone;
};

function toneClass(tone: Tone) {
  return `mf-statcard--${tone}`;
}

export default function StatCard({
  title,
  value,
  subtitle,
  icon,
  tone = "blue",
}: Props) {
  return (
    <div className={`mf-statcard ${toneClass(tone)}`}>
      <div className="mf-statcard__icon" aria-hidden="true">
        {icon ?? "•"}
      </div>

      <div className="mf-statcard__body">
        <div className="mf-statcard__title">{title}</div>
        <div className="mf-statcard__value">{value}</div>
        {subtitle ? <div className="mf-statcard__subtitle">{subtitle}</div> : null}
      </div>
    </div>
  );
}
