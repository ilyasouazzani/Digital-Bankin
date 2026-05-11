export type AccountStatus = 'CREATED' | 'ACTIVATED' | 'SUSPENDED';
export type OperationType = 'DEBIT' | 'CREDIT';

export interface BankAccount {
  id: string;
  balance: number;
  createdAt: Date;
  status: AccountStatus;
  currency: string;
  customerDTO: { id: number; name: string; email: string };
  type: 'CurrentAccount' | 'SavingAccount';
}

export interface CurrentBankAccount extends BankAccount {
  type: 'CurrentAccount';
  overDraft: number;
}

export interface SavingBankAccount extends BankAccount {
  type: 'SavingAccount';
  interestRate: number;
}

export interface AccountOperation {
  id: number;
  operationDate: Date;
  amount: number;
  type: OperationType;
  description: string;
  performedBy?: string;
}

export interface AccountHistory {
  accountId: string;
  balance: number;
  currentPage: number;
  pageSize: number;
  totalPages: number;
  accountOperationDTOs: AccountOperation[];
}
