type Props = {
  icon: string;
  title: string;
  onClick?: () => void;
  disabled?: boolean;
};

export default function IconButton({ icon, title, onClick, disabled = false }: Props) {
  return (
    <button
      type="button"
      className="mf-icon-btn"
      title={title}
      aria-label={title}
      onClick={disabled ? undefined : onClick}
      disabled={disabled}
    >
      <span aria-hidden="true">{icon}</span>
    </button>
  );
}