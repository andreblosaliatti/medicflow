import "./auth.css";
import logo from "../../assets/images/medicflow-logo.png";

export default function BrandHeader() {
  return (
    <div className="brand-header">
      <img className="brand-logo-img" src={logo} alt="MedicFlow" />
    </div>
  );
}