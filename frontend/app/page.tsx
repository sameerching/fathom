import Link from 'next/link';

const links = [
  ['Dashboard', '/dashboard'],
  ['Transactions', '/transactions'],
  ['Upload', '/upload'],
  ['Investments', '/investments'],
  ['Liabilities', '/liabilities']
];

export default function HomePage() {
  return (
    <main className="container">
      <h1>Fathom</h1>
      <p>Understand your money.</p>
      <ul>
        {links.map(([label, href]) => (
          <li key={href}><Link href={href}>{label}</Link></li>
        ))}
      </ul>
    </main>
  );
}
