'use client';
import { Fragment, useEffect, useMemo, useState } from 'react';
import AppShell from '../../components/AppShell';
import PageHeader from '../../components/PageHeader';
import SectionCard from '../../components/SectionCard';
import StatusBadge from '../../components/StatusBadge';
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
    try {
      setError('');
      const res=await getTransactions(userId,{...(filtersOverride??filters),page:String(pageOverride??page),size:sizeOverride??size});
      setRows(res.items);setTotalPages(res.totalPages);
    } catch (e) { setError((e as Error).message); }
  };

  useEffect(()=>{if(!userId)return;getSystemCategories().then(setSystemCategories).catch(e=>setError((e as Error).message));getUserCategories(userId).then(setUserCategories).catch(e=>setError((e as Error).message));},[userId]);
  useEffect(()=>{if(!userId)return;load();},[userId,page,size]);

  return <AppShell><PageHeader title='Transactions' description='Filter, categorize, and manage imported transactions.'/><UserSelector onChange={(id)=>{setUserId(id);setPage(0);setEditingId('');}}/>
    <SectionCard title='Filters'><div className='grid'><FormField label='From'><input type='date' value={filters.from} onChange={e=>setFilters({...filters,from:e.target.value})}/></FormField><FormField label='To'><input type='date' value={filters.to} onChange={e=>setFilters({...filters,to:e.target.value})}/></FormField><FormField label='Merchant'><input value={filters.merchant} onChange={e=>setFilters({...filters,merchant:e.target.value})}/></FormField></div><div className='row'><button onClick={async()=>{setPage(0);await load(0,size);}}>Apply filters</button><button className='secondary' onClick={async()=>{setFilters(initialFilters);setPage(0);await load(0,size,initialFilters);}}>Clear filters</button><select value={size} onChange={async e=>{const n=e.target.value;setSize(n);setPage(0);await load(0,n);}}><option>25</option><option>50</option><option>100</option></select><button onClick={()=>setPage(Math.max(0,page-1))} disabled={page===0}>Previous</button><button onClick={()=>setPage(page+1<totalPages?page+1:page)} disabled={page+1>=totalPages}>Next</button></div></SectionCard>
    <SectionCard title='Bulk category update'><div className='row'><select value={bulkCategoryId} onChange={e=>setBulkCategoryId(e.target.value)}><option value=''>Uncategorized</option>{allCategories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select><button disabled={!selectedIds.length} onClick={async()=>{await bulkUpdateTransactionCategory(userId,selectedIds,bulkCategoryId||null);setSelectedIds([]);await load();}}>Apply</button><span className='badge'>{selectedIds.length} selected</span></div></SectionCard>
    <ErrorMessage message={error}/>
    {rows.length===0?<EmptyState title='No transactions found' description='Try changing filters or upload a CSV first.'/>:<table><thead><tr><th></th><th>Date</th><th>Direction</th><th>Amount</th><th>Type</th><th>Source</th><th>Merchant</th><th>Category</th><th>Actions</th></tr></thead><tbody>{rows.map(r=><Fragment key={r.id}><tr><td><input type='checkbox' checked={selectedIds.includes(r.id)} onChange={()=>setSelectedIds(p=>p.includes(r.id)?p.filter(x=>x!==r.id):[...p,r.id])}/></td><td>{r.transactionDate}</td><td>{r.direction}</td><td>{formatCurrency(r.amount)}</td><td>{r.transactionType}</td><td>{r.source}</td><td>{r.merchant}</td><td><StatusBadge status={allCategories.find(c=>c.id===r.categoryId)?.name||'UNCATEGORIZED'}/></td><td><div className='row'><button className='secondary' onClick={()=>{setEditingId(r.id);setEditForm({...r});}}>Edit</button><button className='danger' onClick={async()=>{if(window.confirm('Delete transaction?')){await deleteTransaction(r.id);await load();}}}>Delete</button></div></td></tr>{editingId===r.id&&editForm&&<tr><td colSpan={9}><SectionCard title='Edit transaction'><div className='grid'><FormField label='Transaction date'><input type='date' value={editForm.transactionDate||''} onChange={e=>setEditForm({...editForm,transactionDate:e.target.value})}/></FormField><FormField label='Amount'><input type='number' value={editForm.amount} onChange={e=>setEditForm({...editForm,amount:Number(e.target.value)})}/></FormField><FormField label='Direction'><select value={editForm.direction||'DEBIT'} onChange={e=>setEditForm({...editForm,direction:e.target.value})}><option>DEBIT</option><option>CREDIT</option></select></FormField><FormField label='Transaction type'><select value={editForm.transactionType||'EXPENSE'} onChange={e=>setEditForm({...editForm,transactionType:e.target.value})}><option>EXPENSE</option><option>INCOME</option><option>INVESTMENT</option><option>LIABILITY_PAYMENT</option></select></FormField><FormField label='Source'><select value={editForm.source||'MANUAL'} onChange={e=>setEditForm({...editForm,source:e.target.value})}><option>MANUAL</option><option>BANK_STATEMENT</option><option>CREDIT_CARD_STATEMENT</option></select></FormField><FormField label='Merchant'><input value={editForm.merchant||''} onChange={e=>setEditForm({...editForm,merchant:e.target.value})}/></FormField><FormField label='Raw description'><input value={editForm.rawDescription||''} onChange={e=>setEditForm({...editForm,rawDescription:e.target.value})}/></FormField><FormField label='Notes'><input value={editForm.notes||''} onChange={e=>setEditForm({...editForm,notes:e.target.value})}/></FormField><FormField label='Category'><select value={editForm.categoryId||''} onChange={e=>setEditForm({...editForm,categoryId:e.target.value||null})}><option value=''>Uncategorized</option>{allCategories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></FormField></div><div className='row'><button onClick={async()=>{await updateTransaction(r.id,editForm);setEditingId('');setEditForm(null);await load();}}>Save changes</button><button className='secondary' onClick={()=>{setEditingId('');setEditForm(null);}}>Cancel</button></div></SectionCard></td></tr>}</Fragment>)}</tbody></table>}
  </AppShell>;
}
