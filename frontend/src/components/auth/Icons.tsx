// src/components/auth/icons.tsx
type IconProps = { size?: number };

export function MailIcon({ size = 22 }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <path
        d="M4 6.8A2.8 2.8 0 0 1 6.8 4h10.4A2.8 2.8 0 0 1 20 6.8v10.4A2.8 2.8 0 0 1 17.2 20H6.8A2.8 2.8 0 0 1 4 17.2V6.8Z"
        stroke="#8B95A1"
        strokeWidth="1.6"
      />
      <path
        d="M5.2 7.2 12 12.1l6.8-4.9"
        stroke="#8B95A1"
        strokeWidth="1.6"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export function LockIcon({ size = 22 }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <path
        d="M7 11V8.8A5 5 0 0 1 12 3.8a5 5 0 0 1 5 5V11"
        stroke="#8B95A1"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <path
        d="M7.2 11h9.6A2.2 2.2 0 0 1 19 13.2v5.6A2.2 2.2 0 0 1 16.8 21H7.2A2.2 2.2 0 0 1 5 18.8v-5.6A2.2 2.2 0 0 1 7.2 11Z"
        stroke="#8B95A1"
        strokeWidth="1.6"
      />
    </svg>
  );
}

export function WarningIcon({ size = 20 }: IconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <path
        d="M12 3.5 22 20.5H2L12 3.5Z"
        fill="#F6C343"
        stroke="#E0AE2C"
        strokeWidth="1"
      />
      <path
        d="M12 9v5"
        stroke="#6A4B00"
        strokeWidth="2"
        strokeLinecap="round"
      />
      <path
        d="M12 17.2h.01"
        stroke="#6A4B00"
        strokeWidth="3"
        strokeLinecap="round"
      />
    </svg>
  );
}

