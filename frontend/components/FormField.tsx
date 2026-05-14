import { ReactNode } from 'react';
export default function FormField({ label, children }: { label: string; children: ReactNode }) {return <label className='field'><span>{label}</span>{children}</label>;}
