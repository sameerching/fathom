import Link from 'next/link';
import AppShell from '../components/AppShell';

export default function HomePage() {
  return (
    <AppShell>
      <div className="panel">
        <h2>Welcome</h2>
        <p>Start with setup, then upload your CSV and explore your dashboard.</p>
        <div className="row">
          <Link href="/setup">Go to Setup</Link>
          <Link href="/dashboard">Go to Dashboard</Link>
        </div>
      </div>
    </AppShell>
  );
}
