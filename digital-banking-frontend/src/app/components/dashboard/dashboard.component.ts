import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { BankAccountService } from '../../services/bank-account.service';
import { CustomerService } from '../../services/customer.service';
import { BankAccount } from '../../models/bank-account.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  totalCustomers = 0;
  totalAccounts = 0;
  totalBalance = 0;
  currentAccountsCount = 0;
  savingAccountsCount = 0;

  // Graphique 1 : Répartition des types de comptes (Doughnut)
  accountTypeChartType: ChartType = 'doughnut';
  accountTypeChartData: ChartData<'doughnut'> = {
    labels: ['Comptes Courants', 'Comptes Épargne'],
    datasets: [{ data: [], backgroundColor: ['#0d6efd','#198754'], borderWidth: 2 }]
  };
  accountTypeChartOptions: ChartConfiguration['options'] = {
    responsive: true, plugins: { legend: { position: 'bottom' } }
  };

  // Graphique 2 : Soldes par client (Bar)
  balanceChartType: ChartType = 'bar';
  balanceChartData: ChartData<'bar'> = {
    labels: [], datasets: [
      { label: 'Solde total (€)', data: [], backgroundColor: '#0d6efd88', borderColor: '#0d6efd', borderWidth: 2 }
    ]
  };
  balanceChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };

  // Graphique 3 : Distribution des soldes (Pie)
  balanceDistChartType: ChartType = 'pie';
  balanceDistChartData: ChartData<'pie'> = {
    labels: ['< 10 000€', '10-50 000€', '50-100 000€', '> 100 000€'],
    datasets: [{ data: [], backgroundColor: ['#dc3545','#ffc107','#0d6efd','#198754'] }]
  };
  balanceDistChartOptions: ChartConfiguration['options'] = {
    responsive: true, plugins: { legend: { position: 'bottom' } }
  };

  constructor(
    private bankAccountService: BankAccountService,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.customerService.getAllCustomers().subscribe(customers => {
      this.totalCustomers = customers.length;
    });

    this.bankAccountService.getAllAccounts().subscribe(accounts => {
      this.totalAccounts = accounts.length;
      this.totalBalance = accounts.reduce((sum, a) => sum + a.balance, 0);
      this.currentAccountsCount = accounts.filter(a => a.type === 'CurrentAccount').length;
      this.savingAccountsCount  = accounts.filter(a => a.type === 'SavingAccount').length;

      // Chart 1 - Répartition types
      this.accountTypeChartData = {
        ...this.accountTypeChartData,
        datasets: [{ ...this.accountTypeChartData.datasets[0],
          data: [this.currentAccountsCount, this.savingAccountsCount] }]
      };

      // Chart 2 - Soldes par client
      const byClient: { [name: string]: number } = {};
      accounts.forEach(a => {
        const name = a.customerDTO.name;
        byClient[name] = (byClient[name] || 0) + a.balance;
      });
      this.balanceChartData = {
        labels: Object.keys(byClient),
        datasets: [{ ...this.balanceChartData.datasets[0], data: Object.values(byClient) }]
      };

      // Chart 3 - Distribution des soldes
      const dist = [0, 0, 0, 0];
      accounts.forEach(a => {
        if (a.balance < 10000) dist[0]++;
        else if (a.balance < 50000) dist[1]++;
        else if (a.balance < 100000) dist[2]++;
        else dist[3]++;
      });
      this.balanceDistChartData = {
        ...this.balanceDistChartData,
        datasets: [{ ...this.balanceDistChartData.datasets[0], data: dist }]
      };
    });
  }
}
