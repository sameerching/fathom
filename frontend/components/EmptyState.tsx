import { ReactNode } from 'react';

export default function EmptyState({ title, description, action }: { title: string; description: string; action?: ReactNode }) {
  return (
    <div className="panel empty-state">
      <h3>{title}</h3>
      <p className="muted">{description}</p>
      {action}
    </div>
  );
}
