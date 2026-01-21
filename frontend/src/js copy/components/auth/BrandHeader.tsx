// src/components/auth/BrandHeader.tsx
import { MedicFlowLogo } from "./icons";
import "./auth.css";

export default function BrandHeader() {
  return (
    <div className="brand-header">
      <div className="brand-logo">
        <MedicFlowLogo />
      </div>

      <div className="brand-name">
        <span className="brand-medic">Medic</span>
        <span className="brand-flow">Flow</span>
      </div>
    </div>
  );
}
