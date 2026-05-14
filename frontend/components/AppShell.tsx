'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ReactNode } from 'react';

const navItems = [
  { label: 'Setup', href: '/setup' },
  { label: 'Dashboard', href: '/dashboard' },
  { label: 'Planning', href: '/planning' },
  { label: 'Transactions', href: '/transactions' },
  { label: 'Categories', href: '/categories' },
  { label: 'Rules', href: '/rules' },
  { label: 'Upload', href: '/upload' },
  { label: 'Investments', href: '/investments' },
  { label: 'Liabilities', href: '/liabilities' }
];

export default function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();

  return (
    <main className="container">
      <header className="app-header panel">
        <Link href="/" className="brand">
          <h1>Fathom</h1>
          <p>Understand your money.</p>
        </Link>
        <nav className="top-nav" aria-label="Primary">
          {navItems.map((item) => (
            <Link key={item.href} href={item.href} className={pathname === item.href ? 'active-nav' : ''}>
              {item.label}
            </Link>
          ))}
        </nav>
      </header>
      <section>{children}</section>
    </main>
  );
}
