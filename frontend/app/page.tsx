const navItems = ['Dashboard', 'Transactions', 'Upload', 'Investments', 'Liabilities', 'Net Worth'];

export default function HomePage() {
  return (
    <main className="container">
      <header>
        <h1>Fathom</h1>
        <p>Understand your money.</p>
      </header>

      <nav aria-label="Primary navigation">
        <ul>
          {navItems.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      </nav>
    </main>
  );
}
