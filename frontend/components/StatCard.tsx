import { ReactNode } from 'react';

export default function StatCard({ label, value, helper }: { label: string; value: ReactNode; helper?: string }) {
  return (
    <div className="card">
      <strong>{label}</strong>
      <div className="stat-value">{value}</div>
      {helper ? <p className="muted">{helper}</p> : null}
    </div>
  );
}
