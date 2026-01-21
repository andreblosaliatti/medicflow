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

export function MedicFlowLogo() {
  return (
    <svg width="78" height="78" viewBox="0 0 78 78" fill="none">
      <defs>
        <linearGradient id="g1" x1="10" y1="10" x2="68" y2="68">
          <stop offset="0" stopColor="#0B4B88" />
          <stop offset="0.55" stopColor="#1FB6FF" />
          <stop offset="1" stopColor="#E6F6FF" />
        </linearGradient>
      </defs>

      <path
        d="M39 6C24.1 6 12 18.1 12 33c0 12.2 8 22.6 19 26.1-4.5-5.2-7.2-10.4-6.5-15.7.9-7.3 8.6-12.3 18.3-12.1 6.7.1 12.4-1.6 16.2-4.3C55.1 14.9 47.6 6 39 6Z"
        fill="url(#g1)"
      />
      <path
        d="M66 39c0 14.9-12.1 27-27 27-7.4 0-14.1-3-18.9-7.9 11.4 2.2 19.7-1.8 25.5-7.2 6.6-6.1 8.7-14.2 9-20.6 3.1 2 6.1 5.6 11.4 8.7Z"
        fill="#0B4B88"
        opacity="0.92"
      />

      <rect x="33.4" y="24" width="11.2" height="30" rx="3" fill="#fff" />
      <rect x="24" y="33.4" width="30" height="11.2" rx="3" fill="#fff" />
    </svg>
  );
}
