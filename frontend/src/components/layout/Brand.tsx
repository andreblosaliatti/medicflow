import logo from "../../assets/images/medicflow-logo-brand.png";
import name from "../../assets/images/medicflow-nome-brand.png";

type Props = {
  variant?: "sidebar" | "header" | "auth";
};

export default function Brand({ variant = "sidebar" }: Props) {
  return (
    <div className={`mf-brand mf-brand--${variant}`}>
      <a href="/dashboard">
        <img
          src={logo}
          alt="MedicFlow"
          className="mf-brand__logo"
          draggable={false}
        />
      </a>
      <a href="/dashboard">
        <img
          src={name}
          alt="MedicFlow"
          className="mf-brand__name-img"
          draggable={false}
        />
      </a>
    </div>
  );
}
