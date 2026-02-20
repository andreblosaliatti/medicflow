import "./auth.css";

type AuthPageLayoutProps = {
  children: React.ReactNode;
  pageClassName?: string;
};

export default function AuthPageLayout({ children, pageClassName }: AuthPageLayoutProps) {
  return <div className={["auth-page", pageClassName].filter(Boolean).join(" ")}>{children}</div>;
}
