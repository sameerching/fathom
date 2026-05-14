'use client';
import { useEffect, useMemo, useState } from 'react';
import AppShell from '../../components/AppShell';
import EmptyState from '../../components/EmptyState';
import ErrorMessage from '../../components/ErrorMessage';
import FormField from '../../components/FormField';
import UserSelector from '../../components/UserSelector';
import { formatCurrency } from '../../lib/format';
import { bulkUpdateTransactionCategory, Category, deleteTransaction, getSystemCategories, getTransactions, getUserCategories, Transaction, updateTransaction } from '../../lib/api';

const initialFilters = { from:'',to:'',merchant:'',transactionType:'',direction:'',minAmount:'',maxAmount:'' };

export default function TransactionsPage(){
  const [userId,setUserId]=useState('');
  const [rows,setRows]=useState<Transaction[]>([]);
  const [page,setPage]=useState(0);
  const [size,setSize]=useState('50');
  const [totalPages,setTotalPages]=useState(0);
  const [error,setError]=useState('');
  const [filters,setFilters]=useState(initialFilters);
  const [systemCategories,setSystemCategories]=useState<Category[]>([]);
  const [userCategories,setUserCategories]=useState<Category[]>([]);
  const [selectedIds,setSelectedIds]=useState<string[]>([]);
  const [bulkCategoryId,setBulkCategoryId]=useState('');
  const [editingId,setEditingId]=useState('');
  const [editForm,setEditForm]=useState<any>(null);
  const allCategories=useMemo(()=>[...systemCategories,...userCategories],[systemCategories,userCategories]);

  const load=async(pageOverride?:number,sizeOverride?:string,filtersOverride?:typeof initialFilters)=>{
    if(!userId)return;
    const effectivePage = pageOverride ?? page;
    const effectiveSize = sizeOverride ?? size;
    const effectiveFilters = filtersOverride ?? filters;
    try{setError(''); const res=await getTransactions(userId,{...effectiveFilters,page:String(effectivePage),size:effectiveSize}); setRows(res.items); setTotalPages(res.totalPages);}catch(e){setError((e as Error).message);}  
  };

  useEffect(()=>{if(!userId)return; getSystemCategories().then(setSystemCategories).catch(e=>setError((e as Error).message)); getUserCategories(userId).then(setUserCategories).catch(e=>setError((e as Error).message));},[userId]);
  useEffect(()=>{ if(!userId) return; load(); },[userId,page,size]);

  const beginEdit=(r:Transaction)=>{setEditingId(r.id); setEditForm({...r});};
  const toggle=(id:string)=>setSelectedIds(prev=>prev.includes(id)?prev.filter(x=>x!==id):[...prev,id]);

  return <AppShell><h2>Transactions</h2><UserSelector onChange={(id)=>{setUserId(id); setPage(0);}}/>
  <div className='panel'><div className='grid'>
    <FormField label='From'><input type='date' value={filters.from} onChange={e=>setFilters({...filters,from:e.target.value})}/></FormField>
    <FormField label='To'><input type='date' value={filters.to} onChange={e=>setFilters({...filters,to:e.target.value})}/></FormField>
    <FormField label='Merchant'><input value={filters.merchant} onChange={e=>setFilters({...filters,merchant:e.target.value})}/></FormField>
  </div><div className='row'>
    <button onClick={async()=>{setPage(0); await load(0,size);}}>Apply filters</button>
    <button type='button' className='secondary' onClick={async()=>{setFilters(initialFilters); setPage(0); await load(0,size,initialFilters);}}>Clear filters</button>
    <select value={size} onChange={async e=>{const next=e.target.value; setSize(next); setPage(0); await load(0,next);}}><option>25</option><option>50</option><option>100</option></select>
    <button onClick={()=>setPage(Math.max(0,page-1))} disabled={page===0}>Previous</button>
    <button onClick={()=>setPage(page+1<totalPages?page+1:page)} disabled={page+1>=totalPages}>Next</button>
  </div></div>

  <div className='panel'><h3>Bulk category update</h3><select value={bulkCategoryId} onChange={e=>setBulkCategoryId(e.target.value)}><option value=''>Uncategorized</option>{allCategories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select><button disabled={!userId||selectedIds.length===0} onClick={async()=>{await bulkUpdateTransactionCategory(userId,selectedIds,bulkCategoryId||null); setSelectedIds([]); await load();}}>Apply bulk category</button></div><ErrorMessage message={error} />{rows.length===0?<EmptyState title='No transactions found' description='Try changing filters or upload a CSV first.' />:<table><thead><tr><th/><th>date</th><th>direction</th><th>amount</th><th>type</th><th>source</th><th>merchant</th><th>category</th><th>actions</th></tr></thead><tbody>{rows.map(r=><><tr key={r.id}><td><input type='checkbox' checked={selectedIds.includes(r.id)} onChange={()=>toggle(r.id)}/></td><td>{r.transactionDate}</td><td>{r.direction}</td><td>{formatCurrency(r.amount)}</td><td>{r.transactionType}</td><td>{r.source}</td><td>{r.merchant}</td><td>{allCategories.find(c=>c.id===r.categoryId)?.name||'Uncategorized'}</td><td><button onClick={()=>beginEdit(r)}>Edit</button><button onClick={async()=>{if(window.confirm('Delete transaction?')){await deleteTransaction(r.id); await load();}}}>Delete</button></td></tr>{editingId===r.id&&editForm&&<tr><td colSpan={9}><div className='grid'><input type='date' value={editForm.transactionDate} onChange={e=>setEditForm({...editForm,transactionDate:e.target.value})}/><input value={editForm.amount} onChange={e=>setEditForm({...editForm,amount:Number(e.target.value)})}/><select value={editForm.direction} onChange={e=>setEditForm({...editForm,direction:e.target.value})}><option>DEBIT</option><option>CREDIT</option></select><select value={editForm.transactionType} onChange={e=>setEditForm({...editForm,transactionType:e.target.value})}><option>EXPENSE</option><option>INCOME</option><option>INVESTMENT</option><option>LIABILITY_PAYMENT</option></select><select value={editForm.source||'MANUAL'} onChange={e=>setEditForm({...editForm,source:e.target.value})}><option>MANUAL</option><option>BANK_STATEMENT</option><option>CREDIT_CARD_STATEMENT</option></select><input value={editForm.merchant||''} onChange={e=>setEditForm({...editForm,merchant:e.target.value})}/><input value={editForm.rawDescription||''} onChange={e=>setEditForm({...editForm,rawDescription:e.target.value})}/><input value={editForm.notes||''} onChange={e=>setEditForm({...editForm,notes:e.target.value})}/><select value={editForm.categoryId||''} onChange={e=>setEditForm({...editForm,categoryId:e.target.value||null})}><option value=''>Uncategorized</option>{allCategories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></div><button onClick={async()=>{await updateTransaction(r.id,editForm); setEditingId(''); await load();}}>Save</button><button onClick={()=>setEditingId('')}>Cancel</button></td></tr>}</>)}</tbody></table>}</AppShell>
}
