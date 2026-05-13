'use client';

import Link from 'next/link';
import { FormEvent, useEffect, useState } from 'react';
import { Account, createAccount, createUser, CreateAccountRequest, CreateUserRequest, getUsers, User } from '../../../lib/api';

const accountTypes = ['BANK_ACCOUNT', 'CREDIT_CARD', 'BROKERAGE', 'CASH', 'LOAN', 'MANUAL'];

export default function SetupPage() {
  const [userForm, setUserForm] = useState<CreateUserRequest>({ name: '', email: '', status: 'ACTIVE' });
  const [accountForm, setAccountForm] = useState<CreateAccountRequest>({ name: '', institutionName: '', accountType: 'BANK_ACCOUNT', currency: 'INR', maskedIdentifier: '' });
  const [userId, setUserId] = useState('');
  const [createdUser, setCreatedUser] = useState<User | null>(null);
  const [createdAccount, setCreatedAccount] = useState<Account | null>(null);
  const [users, setUsers] = useState<User[]>([]);

  const [loadingUser, setLoadingUser] = useState(false);
  const [loadingAccount, setLoadingAccount] = useState(false);
  const [errorUser, setErrorUser] = useState('');
  const [errorAccount, setErrorAccount] = useState('');
  const [successUser, setSuccessUser] = useState('');
  const [successAccount, setSuccessAccount] = useState('');

  useEffect(() => {
    const saved = localStorage.getItem('fathom.userId') ?? '';
    setUserId(saved);
  }, []);

  useEffect(() => {
    getUsers().then(setUsers).catch(() => setUsers([]));
  }, []);

  const onCreateUser = async (e: FormEvent) => {
    e.preventDefault();
    setLoadingUser(true);
    setErrorUser('');
    setSuccessUser('');
    try {
      const created = await createUser(userForm);
      setCreatedUser(created);
      setUserId(created.id);
      setUsers((prev) => [created, ...prev]);
      localStorage.setItem('fathom.userId', created.id);
      setSuccessUser(`User created successfully. Saved user ID ${created.id}.`);
    } catch (err) {
      setErrorUser((err as Error).message);
    } finally {
      setLoadingUser(false);
    }
  };

  const onCreateAccount = async (e: FormEvent) => {
    e.preventDefault();
    if (!userId) return;
    setLoadingAccount(true);
    setErrorAccount('');
    setSuccessAccount('');
    try {
      const payload = Object.fromEntries(Object.entries(accountForm).filter(([, value]) => value !== '')) as CreateAccountRequest;
      const created = await createAccount(userId, payload);
      setCreatedAccount(created);
      setSuccessAccount(`Account created successfully for user ${userId}.`);
    } catch (err) {
      setErrorAccount((err as Error).message);
    } finally {
      setLoadingAccount(false);
    }
  };

  return (
    <main className="container">
      <h2>Setup</h2>
      <p>Create a user and first account so you can start uploading and viewing data.</p>

      <section className="panel">
        <h3>Section A: Create User</h3>
        <form onSubmit={onCreateUser}>
          <input placeholder="Name" value={userForm.name} onChange={(e) => setUserForm({ ...userForm, name: e.target.value })} required />
          <input type="email" placeholder="Email" value={userForm.email} onChange={(e) => setUserForm({ ...userForm, email: e.target.value })} required />
          <select value={userForm.status} onChange={(e) => setUserForm({ ...userForm, status: e.target.value })}>
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
          </select>
          <button type="submit" disabled={loadingUser}>{loadingUser ? 'Creating user...' : 'Create User'}</button>
        </form>
        {successUser && <p>{successUser}</p>}
        {errorUser && <p className="warn">{errorUser}</p>}
        {createdUser && <p>Created user ID: <strong>{createdUser.id}</strong></p>}
        {!!users.length && <p>Existing users found: {users.length}</p>}
      </section>

      <section className="panel">
        <h3>Section B: Create Account</h3>
        {!userId && <p className="warn">Create a user first or save a user ID before creating an account.</p>}
        <form onSubmit={onCreateAccount}>
          <input placeholder="Account name" value={accountForm.name} onChange={(e) => setAccountForm({ ...accountForm, name: e.target.value })} disabled={!userId} required />
          <input placeholder="Institution name" value={accountForm.institutionName ?? ''} onChange={(e) => setAccountForm({ ...accountForm, institutionName: e.target.value })} disabled={!userId} />
          <select value={accountForm.accountType} onChange={(e) => setAccountForm({ ...accountForm, accountType: e.target.value })} disabled={!userId}>
            {accountTypes.map((accountType) => <option key={accountType} value={accountType}>{accountType}</option>)}
          </select>
          <input placeholder="Currency" value={accountForm.currency ?? 'INR'} onChange={(e) => setAccountForm({ ...accountForm, currency: e.target.value })} disabled={!userId} />
          <input placeholder="Masked identifier (optional)" value={accountForm.maskedIdentifier ?? ''} onChange={(e) => setAccountForm({ ...accountForm, maskedIdentifier: e.target.value })} disabled={!userId} />
          <button type="submit" disabled={!userId || loadingAccount}>{loadingAccount ? 'Creating account...' : 'Create Account'}</button>
        </form>
        {successAccount && <p>{successAccount}</p>}
        {errorAccount && <p className="warn">{errorAccount}</p>}
        {createdAccount && <pre>{JSON.stringify(createdAccount, null, 2)}</pre>}
      </section>

      <section className="panel">
        <h3>Section C: Next Actions</h3>
        <ul>
          <li><Link href="/upload">Upload CSV</Link></li>
          <li><Link href="/dashboard">Dashboard</Link></li>
          <li><Link href="/transactions">Transactions</Link></li>
        </ul>
      </section>
    </main>
  );
}
