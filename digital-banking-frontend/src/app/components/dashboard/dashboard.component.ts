import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import {
  Chart, ArcElement, BarElement, CategoryScale, LinearScale,
  Tooltip, Legend, DoughnutController, PieController, BarController
} from 'chart.js';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { BankAccountService } from '../../services/bank-account.service';
import { CustomerService } from '../../services/customer.service';

// Enregistrement global Chart.js
Chart.register(
  ArcElement, BarElement, CategoryScale, LinearScale,
  Tooltip, Legend, DoughnutController, PieController, BarController
);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  totalCustomers = 0;
  totalAccounts  = 0;
  totalBalance   = 0;
  currentAccountsCount = 0;
  savingAccountsCount  = 0;
  loading = true;

  // Chart 1 — Répartition types de comptes (Doughnut)
  typeChartType: ChartType = 'doughnut';
  typeChartData: ChartData<'doughnut'> = {
    labels: ['Comptes Courants', 'Comptes Épargne'],
    datasets: [{ data: [0, 0], backgroundColor: ['#0d6efd', '#198754'], borderWidth: 2 }]
  };
  typeChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
  };

  // Chart 2 — Solde par client (Bar)
  barChartType: ChartType = 'bar';
  barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [{
      label: 'Solde total (€)', data: [],
      backgroundColor: 'rgba(13,110,253,0.5)',
      borderColor: '#0d6efd', borderWidth: 2
    }]
  };
  barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };

  // Chart 3 — Distribution des soldes (Pie)
  pieChartType: ChartType = 'pie';
  pieChartData: ChartData<'pie'> = {
    labels: ['< 10 000€', '10–50 000€', '50–100 000€', '> 100 000€'],
    datasets: [{
      data: [0, 0, 0, 0],
      backgroundColor: ['#dc3545', '#ffc107', '#0d6efd', '#198754']
    }]
  };
  pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
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
      this.loading = false;
      this.totalAccounts          = accounts.length;
      this.totalBalance           = accounts.reduce((s, a) => s + a.balance, 0);
      this.currentAccountsCount   = accounts.filter(a => a.type === 'CurrentAccount').length;
      this.savingAccountsCount    = accounts.filter(a => a.type === 'SavingAccount').length;

      // Chart 1
      this.typeChartData = {
        ...this.typeChartData,
        datasets: [{ ...this.typeChartData.datasets[0],
          data: [this.currentAccountsCount, this.savingAccountsCount] }]
      };

      // Chart 2 — regroupement par client
      const byClient: Record<string, number> = {};
      accounts.forEach(a => {
        const n = a.customerDTO.name;
        byClient[n] = (byClient[n] || 0) + a.balance;
      });
      this.barChartData = {
        labels: Object.keys(byClient),
        datasets: [{ ...this.barChartData.datasets[0], data: Object.values(byClient) }]
      };

      // Chart 3 — distribution soldes
      const dist = [0, 0, 0, 0];
      accounts.forEach(a => {
        if      (a.balance < 10000)  dist[0]++;
        else if (a.balance < 50000)  dist[1]++;
        else if (a.balance < 100000) dist[2]++;
        else                          dist[3]++;
      });
      this.pieChartData = {
        ...this.pieChartData,
        datasets: [{ ...this.pieChartData.datasets[0], data: dist }]
      };
    });
  }
}
