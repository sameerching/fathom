import React from 'react';
import { formatCurrency } from '../lib/format';

export const WidgetCard=({title,children,right}:{title:string;children:React.ReactNode;right?:React.ReactNode})=><section className='widget-card'><div className='row' style={{justifyContent:'space-between'}}><h3>{title}</h3>{right}</div>{children}</section>;
export const InsightCard=({label,value,tone}:{label:string;value:string;tone?:'pos'|'neg'|'neu'})=><div className='insight-card'><div className='subtle-text'>{label}</div><div className={tone==='pos'?'money-positive':tone==='neg'?'money-negative':''} style={{fontSize:'1.1rem',fontWeight:700}}>{value}</div></div>;
export const TransactionAvatar=({label}:{label:string})=><div style={{width:34,height:34,borderRadius:999,display:'grid',placeItems:'center',background:'rgba(122,143,255,.25)',fontWeight:700}}>{(label||'?').slice(0,1).toUpperCase()}</div>;
export const CategoryPill=({label}:{label?:string|null})=><span className='pill'>{label||'Uncategorized'}</span>;
export const AmountText=({value}:{value:number})=><span className={value>=0?'amount-pos':'amount-neg'}>{formatCurrency(value)}</span>;
export const QuickActionCard=({title,desc,action}:{title:string;desc:string;action:React.ReactNode})=><div className='account-card'><h4>{title}</h4><div className='subtle-text'>{desc}</div><div style={{marginTop:8}}>{action}</div></div>;
