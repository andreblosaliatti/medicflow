import type { HTMLAttributes, ReactNode } from "react";
import "./styles.css";

type AppPageProps = HTMLAttributes<HTMLDivElement> & {
  header?: ReactNode;
  contentClassName?: string;
};

export default function AppPage({
  header,
  children,
  className,
  contentClassName,
  ...rest
}: AppPageProps) {
  return (
    <div className={["mf-page", className].filter(Boolean).join(" ")} {...rest}>
      {header}
      <div className={["mf-page__content", contentClassName].filter(Boolean).join(" ")}>{children}</div>
    </div>
  );
}
