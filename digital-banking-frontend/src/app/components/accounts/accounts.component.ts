import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BankAccountService } from '../../services/bank-account.service';
import { AccountHistory, BankAccount } from '../../models/bank-account.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
  accounts: BankAccount[] = [];
  selectedAccount: BankAccount | null = null;
  accountHistory: AccountHistory | null = null;
  loading = false;
  historyLoading = false;
  currentPage = 0;
  pageSize = 5;
  customerId: number | null = null;

  constructor(private bankAccountService: BankAccountService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.customerId = params['customerId'] ? +params['customerId'] : null;
      this.loadAccounts();
    });
  }

  loadAccounts(): void {
    this.loading = true;
    const obs = this.customerId
      ? this.bankAccountService.getCustomerAccounts(this.customerId)
      : this.bankAccountService.getAllAccounts();
    obs.subscribe({
      next: data => { this.accounts = data; this.loading = false; },
      error: () => this.loading = false
    });
  }

  selectAccount(account: BankAccount): void {
    this.selectedAccount = account;
    this.currentPage = 0;
    this.loadHistory();
  }

  loadHistory(): void {
    if (!this.selectedAccount) return;
    this.historyLoading = true;
    this.bankAccountService.getAccountHistory(this.selectedAccount.id, this.currentPage, this.pageSize)
      .subscribe({
        next: data => { this.accountHistory = data; this.historyLoading = false; },
        error: () => this.historyLoading = false
      });
  }

  goToPage(page: number): void { this.currentPage = page; this.loadHistory(); }

  getPages(): number[] {
    if (!this.accountHistory) return [];
    return Array.from({ length: this.accountHistory.totalPages }, (_, i) => i);
  }

  getAccountTypeLabel(type: string): string {
    return type === 'CurrentAccount' ? 'Compte Courant' : 'Compte Épargne';
  }

  getAccountTypeBadge(type: string): string {
    return type === 'CurrentAccount' ? 'bg-primary' : 'bg-success';
  }
}
