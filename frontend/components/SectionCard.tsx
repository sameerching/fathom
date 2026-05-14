import { ReactNode } from 'react';
export default function SectionCard({title, children}:{title?:string;children:ReactNode}){return <section className='panel'>{title && <h3>{title}</h3>}{children}</section>}
