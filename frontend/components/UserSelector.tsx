'use client';
import { useEffect, useState } from 'react';

export default function UserSelector({ onChange }: { onChange: (value: string) => void }) {
  const [value, setValue] = useState('');
  useEffect(() => {
    const saved = localStorage.getItem('fathom.userId') ?? '';
    setValue(saved);
    onChange(saved);
  }, [onChange]);

  return (
    <div className="panel">
      <label htmlFor="userId">Current User ID</label>
      <div className="row">
        <input id="userId" value={value} onChange={(e) => setValue(e.target.value)} placeholder="Paste user UUID" />
        <button type="button" onClick={() => { localStorage.setItem('fathom.userId', value.trim()); onChange(value.trim()); }}>Save</button>
      </div>
      {!value.trim() && <p className="warn">User ID required for API calls.</p>}
    </div>
  );
}
