'use client';
import { useEffect, useState } from 'react';
import { compactId } from '../lib/format';

export default function UserSelector({ onChange }: { onChange: (value: string) => void }) {
  const [value, setValue] = useState('');

  useEffect(() => {
    const saved = localStorage.getItem('fathom.userId') ?? '';
    setValue(saved);
    onChange(saved);
  }, [onChange]);

  const trimmed = value.trim();

  return (
    <div className="panel">
      <label htmlFor="userId">Current User ID</label>
      <div className="row">
        <input id="userId" value={value} onChange={(e) => setValue(e.target.value)} placeholder="Paste user UUID" />
        <button type="button" onClick={() => { localStorage.setItem('fathom.userId', trimmed); onChange(trimmed); }}>
          Save
        </button>
        <button type="button" className="secondary" onClick={() => { localStorage.removeItem('fathom.userId'); setValue(''); onChange(''); }}>
          Clear
        </button>
      </div>
      {trimmed ? <p>Using saved user ID: <strong>{compactId(trimmed)}</strong></p> : <p className="warn">User ID required for API calls.</p>}
    </div>
  );
}
