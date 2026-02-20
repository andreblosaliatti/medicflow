import { Link } from "react-router-dom";
import "./auth.css";

type AuthLinkDividerProps = {
  to: string;
  children: React.ReactNode;
  className?: string;
  linkClassName?: string;
};

export default function AuthLinkDivider({ to, children, className, linkClassName }: AuthLinkDividerProps) {
  return (
    <div className={["auth-link-divider", className].filter(Boolean).join(" ")}>
      <span className="line" />
      <Link className={["auth-link", linkClassName].filter(Boolean).join(" ")} to={to}>
        {children}
      </Link>
      <span className="line" />
    </div>
  );
}
