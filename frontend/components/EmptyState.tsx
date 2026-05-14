import { ReactNode } from 'react';
export default function EmptyState({ title, description, action }: { title: string; description?: string; action?: ReactNode }) {return <div className='empty-state'><h3>{title}</h3>{description && <p className='muted'>{description}</p>}{action && <div>{action}</div>}</div>}
