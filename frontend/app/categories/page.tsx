'use client';
import { FormEvent, useEffect, useState } from 'react';
import AppShell from '../../components/AppShell';
import EmptyState from '../../components/EmptyState';
import ErrorMessage from '../../components/ErrorMessage';
import FormField from '../../components/FormField';
import UserSelector from '../../components/UserSelector';
import { Category, createCategory, getSystemCategories, getUserCategories } from '../../lib/api';
const categoryTypes = ['INCOME', 'EXPENSE', 'INVESTMENT', 'TRANSFER', 'LIABILITY_PAYMENT', 'ADJUSTMENT'];
export default function CategoriesPage() { const [userId, setUserId] = useState(''); const [systemCategories, setSystemCategories] = useState<Category[]>([]); const [userCategories, setUserCategories] = useState<Category[]>([]); const [name, setName] = useState(''); const [categoryType, setCategoryType] = useState(''); const [error, setError] = useState('');
  useEffect(() => { getSystemCategories().then(setSystemCategories).catch((e) => setError((e as Error).message)); }, []);
  useEffect(() => { if (userId) getUserCategories(userId).then(setUserCategories).catch((e) => setError((e as Error).message)); }, [userId]);
  const onSubmit = async (e: FormEvent) => { e.preventDefault(); if (!userId || !name || !categoryType) return; try { setError(''); await createCategory(userId, { name, categoryType, parentCategoryId: null }); setName(''); setCategoryType(''); setUserCategories(await getUserCategories(userId)); } catch (err) { setError((err as Error).message); } };
  return <AppShell><h2>Categories</h2><UserSelector onChange={setUserId} /><ErrorMessage message={error} /><div className='panel'><h3>Create category</h3><form onSubmit={onSubmit}><div className='grid'><FormField label='Name'><input value={name} onChange={(e) => setName(e.target.value)} /></FormField><FormField label='Type'><select value={categoryType} onChange={(e) => setCategoryType(e.target.value)}><option value=''>Select</option>{categoryTypes.map((t) => <option key={t} value={t}>{t}</option>)}</select></FormField></div><button disabled={!userId || !name || !categoryType}>Create</button></form></div><div className='panel'><h3>System categories</h3>{systemCategories.length===0?<EmptyState title='No system categories' description='No categories found.'/>:<table><thead><tr><th>Name</th><th>Type</th></tr></thead><tbody>{systemCategories.map(c=><tr key={c.id}><td>{c.name}</td><td>{c.categoryType}</td></tr>)}</tbody></table>}</div><div className='panel'><h3>User categories</h3>{!userId?<EmptyState title='Select a user' description='Choose a user to view custom categories.'/>:userCategories.length===0?<EmptyState title='No user categories' description='Create one above.'/>:<table><thead><tr><th>Name</th><th>Type</th></tr></thead><tbody>{userCategories.map(c=><tr key={c.id}><td>{c.name}</td><td>{c.categoryType}</td></tr>)}</tbody></table>}</div></AppShell>;
}
