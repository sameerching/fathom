const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';
async function request<T>(path: string, init?: RequestInit): Promise<T> { const headers: Record<string, string> = { ...(init?.headers as Record<string, string> | undefined) }; if (!(init?.body instanceof FormData)) headers['Content-Type'] = headers['Content-Type'] ?? 'application/json'; const response = await fetch(`${API_BASE_URL}${path}`, { ...init, headers }); if (!response.ok) throw new Error((await response.text()) || `Request failed ${response.status}`); return response.json() as Promise<T>; }
export type User = { id: string; name: string; email: string; status: string; createdAt: string; updatedAt: string };
export type CreateUserRequest = { name: string; email: string; status: string };
export type CreateAccountRequest = { name: string; institutionName?: string; accountType: string; currency?: string; maskedIdentifier?: string };
export type Category = { id:string; userId:string|null; name:string; categoryType:string; systemDefault:boolean; active:boolean };
export type CreateCategoryRequest = { name:string; categoryType:string; parentCategoryId:string|null };
export type MonthlySummary = { income:number;expenses:number;investments:number;liabilityPayments:number;netCashFlow:number;savingsRate:number };
export type CategoryBreakdownItem = { categoryName:string;amount:number;transactionCount:number };
export type NetWorthSummary = { totalAssets:number;totalLiabilities:number;netWorth:number };
export type Transaction = { id:string;transactionDate:string;direction:string;amount:number;transactionType:string;merchant:string|null;rawDescription:string|null;accountId:string;categoryId:string|null };
export type ImportSummary = { status:string;totalRows:number;createdCount:number;skippedDuplicateCount:number;failedCount:number;errors:{rowNumber:number;message:string}[] };
export type Account = { id:string;name:string;accountType:string;institutionName?:string;currency?:string;maskedIdentifier?:string };
export type InvestmentHolding = { id:string;assetType:string;name:string;provider:string|null;symbol:string|null;currency:string|null;investedAmount:number|null;currentValue:number|null;asOfDate:string|null };
export type Liability = { id:string;liabilityType:string;name:string;lender:string|null;currency:string|null;principalAmount:number|null;outstandingAmount:number;interestRate:number|null;emiAmount:number|null;startDate:string|null;endDate:string|null };
export type CategoryRule = { id:string; userId:string; name:string; priority:number; ruleField:string; matchOperator:string; matchValue:string; categoryId:string; transactionType:string|null; direction:string|null; active:boolean; createdAt:string; updatedAt:string };
export type CreateCategoryRuleRequest = { name:string; priority?:number; ruleField:string; matchOperator:string; matchValue:string; categoryId:string; transactionType?:string|null; direction?:string|null; active?:boolean };
export type ApplyCategoryRulesResponse = { matchedCount:number; updatedCount:number; skippedCount:number };

export type RecurringTransaction = { id:string;userId:string;accountId:string|null;categoryId:string|null;name:string;amount:number;direction:string;transactionType:string;frequency:string;dayOfMonth:number|null;startDate:string;endDate:string|null;active:boolean;notes:string|null };
export type CreateRecurringTransactionRequest = { accountId?:string|null;categoryId?:string|null;name:string;amount:number;direction:string;transactionType:string;frequency:string;dayOfMonth?:number|null;startDate:string;endDate?:string|null;active?:boolean;notes?:string|null };
export type MonthlyPlanningSummary = { userId:string;month:string;plannedIncome:number;actualIncome:number;incomeVariance:number;plannedExpenses:number;actualExpenses:number;expensesVariance:number;plannedInvestments:number;actualInvestments:number;investmentsVariance:number;plannedLiabilityPayments:number;actualLiabilityPayments:number;liabilityPaymentsVariance:number;plannedNetCashFlow:number;actualNetCashFlow:number;netCashFlowVariance:number };

export const createUser = (payload: CreateUserRequest) => request<User>('/api/users', { method: 'POST', body: JSON.stringify(payload) }); export const getUsers = () => request<User[]>('/api/users'); export const createAccount = (userId: string, payload: CreateAccountRequest) => request<Account>(`/api/users/${userId}/accounts`, { method: 'POST', body: JSON.stringify(payload) });
export const getSystemCategories = ()=>request<Category[]>('/api/categories/system');
export const getUserCategories = (userId:string)=>request<Category[]>(`/api/users/${userId}/categories`);
export const createCategory = (userId:string,payload:CreateCategoryRequest)=>request<Category>(`/api/users/${userId}/categories`,{method:'POST',body:JSON.stringify(payload)});
export const updateTransactionCategory = (transactionId:string,categoryId:string|null)=>request<Transaction>(`/api/transactions/${transactionId}/category`,{method:'PATCH',body:JSON.stringify({categoryId})});
export const getMonthlySummary = (userId:string, month:string)=>request<MonthlySummary>(`/api/users/${userId}/dashboard/monthly-summary?month=${month}`);
export const getCategoryBreakdown = (userId:string,from:string,to:string,type:string)=>request<CategoryBreakdownItem[]>(`/api/users/${userId}/dashboard/category-breakdown?${new URLSearchParams({from,to,type})}`);
export const getNetWorth = (userId:string)=>request<NetWorthSummary>(`/api/users/${userId}/dashboard/net-worth`);
export const getTransactions = (userId:string, filters:Record<string,string>)=>{ const p=new URLSearchParams(); Object.entries(filters).forEach(([k,v])=>v&&p.set(k,v)); return request<Transaction[]>(`/api/users/${userId}/transactions?${p}`); };
export const uploadTransactionsCsv = async (userId:string,accountId:string,file:File,source:string)=>{ const fd=new FormData(); fd.append('file',file); return request<ImportSummary>(`/api/users/${userId}/accounts/${accountId}/transaction-imports?source=${source}`,{method:'POST',body:fd}); };
export const getUserAccounts = (userId:string)=>request<Account[]>(`/api/users/${userId}/accounts`);
export const getInvestmentHoldings = (userId:string)=>request<InvestmentHolding[]>(`/api/users/${userId}/investment-holdings`);
export const createInvestmentHolding = (userId:string,payload:Record<string,unknown>)=>request<InvestmentHolding>(`/api/users/${userId}/investment-holdings`,{method:'POST',body:JSON.stringify(payload)});
export const getLiabilities = (userId:string)=>request<Liability[]>(`/api/users/${userId}/liabilities`);
export const createLiability = (userId:string,payload:Record<string,unknown>)=>request<Liability>(`/api/users/${userId}/liabilities`,{method:'POST',body:JSON.stringify(payload)});

export const getCategoryRules = (userId:string)=>request<CategoryRule[]>(`/api/users/${userId}/category-rules`);
export const createCategoryRule = (userId:string,payload:CreateCategoryRuleRequest)=>request<CategoryRule>(`/api/users/${userId}/category-rules`,{method:'POST',body:JSON.stringify(payload)});
export const updateCategoryRule = (ruleId:string,payload:CreateCategoryRuleRequest)=>request<CategoryRule>(`/api/category-rules/${ruleId}`,{method:'PATCH',body:JSON.stringify(payload)});
export const deactivateCategoryRule = (ruleId:string)=>request<void>(`/api/category-rules/${ruleId}/deactivate`,{method:'PATCH'});
export const applyCategoryRules = (userId:string, params:{from?:string;to?:string;onlyUncategorized?:boolean})=>{ const p=new URLSearchParams(); if(params.from) p.set('from',params.from); if(params.to) p.set('to',params.to); p.set('onlyUncategorized', String(params.onlyUncategorized ?? true)); return request<ApplyCategoryRulesResponse>(`/api/users/${userId}/category-rules/apply?${p}`,{method:'POST'}); };

export const getRecurringTransactions = (userId:string)=>request<RecurringTransaction[]>(`/api/users/${userId}/recurring-transactions`);
export const createRecurringTransaction = (userId:string,payload:CreateRecurringTransactionRequest)=>request<RecurringTransaction>(`/api/users/${userId}/recurring-transactions`,{method:'POST',body:JSON.stringify(payload)});
export const deactivateRecurringTransaction = (id:string)=>request<RecurringTransaction>(`/api/recurring-transactions/${id}/deactivate`,{method:'PATCH'});
export const getMonthlyPlanningSummary = (userId:string,month:string)=>request<MonthlyPlanningSummary>(`/api/users/${userId}/planning/monthly-summary?month=${month}`);
