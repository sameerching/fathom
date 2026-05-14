'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ReactNode } from 'react';
const groups=[{title:'Overview',items:[['🏠','Dashboard','/dashboard'],['🧭','Planning','/planning'],['🎯','Budgets','/budgets']]},{title:'Money',items:[['💳','Transactions','/transactions'],['🏷️','Categories','/categories'],['🧠','Rules','/rules'],['📤','Upload','/upload'],['🧾','Imports','/imports']]},{title:'Net Worth',items:[['📈','Investments','/investments'],['🏦','Liabilities','/liabilities']]},{title:'Setup',items:[['🛠️','Setup','/setup']]}];
export default function AppShell({ children }: { children: ReactNode }) { const pathname=usePathname();
return <main className='container app-shell'><aside className='sidebar panel'><Link href='/' className='brand'><h2>Fathom</h2><p className='muted'>Understand your money</p></Link>{groups.map(g=><div key={g.title} className='nav-group'><div className='nav-group-title'>{g.title}</div><nav className='top-nav'>{g.items.map(([i,l,h])=><Link key={String(h)} href={String(h)} className={pathname===h?'active-nav':''}>{i} {l}</Link>)}</nav></div>)}<div className='muted' style={{marginTop:'1rem'}}>Local MVP</div></aside><section className='main-content'>{children}</section></main>;
}
