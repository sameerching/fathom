'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ReactNode } from 'react';

const primaryNav = [
  ['Dashboard', '/dashboard'],
  ['Planning', '/planning'],
  ['Transactions', '/transactions'],
  ['Budgets', '/budgets'],
  ['Upload', '/upload'],
  ['More', '/categories']
];

const secondaryNav = [
  ['Categories', '/categories'],
  ['Rules', '/rules'],
  ['Imports', '/imports'],
  ['Investments', '/investments'],
  ['Liabilities', '/liabilities'],
  ['Setup', '/setup']
];

export default function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const active = (href: string) => pathname === href;

  return (
    <main className='app-shell-top'>
      <header className='app-header glass-panel'>
        <div className='brand-lockup'>
          <div className='brand-mark'>F</div>
          <div>
            <div className='brand-title'>Fathom</div>
            <div className='brand-subtitle'>Money OS</div>
          </div>
        </div>
        <nav className='nav-tabs'>
          {primaryNav.map(([label, href]) => (
            <Link key={href} href={href} className={`nav-tab ${active(href) ? 'nav-tab-active' : ''}`}>
              {label}
            </Link>
          ))}
        </nav>
        <div className='row'>
          <input aria-label='search' placeholder='Search transactions...' className='search-input' />
          <span className='action-pill'>Local MVP</span>
          <div className='profile-dot'>U</div>
        </div>
      </header>
      <nav className='sub-nav'>
        {secondaryNav.map(([label, href]) => (
          <Link key={href} href={href} className={`nav-tab ${active(href) ? 'nav-tab-active' : ''}`}>
            {label}
          </Link>
        ))}
      </nav>
      <section className='page-container'>{children}</section>
    </main>
  );
}
