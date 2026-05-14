import { ReactNode } from 'react';
export default function PageHeader({ title, description, action }: { title: string; description?: string; action?: ReactNode }) {
  return <div className='panel row' style={{justifyContent:'space-between', alignItems:'end'}}><div><h2>{title}</h2>{description && <p className='muted'>{description}</p>}</div>{action}</div>;
}
