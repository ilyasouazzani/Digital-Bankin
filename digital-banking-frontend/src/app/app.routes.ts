import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'dashboard',  canActivate: [authGuard], loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'customers',  canActivate: [authGuard], loadComponent: () => import('./components/customers/customers.component').then(m => m.CustomersComponent) },
  { path: 'accounts',   canActivate: [authGuard], loadComponent: () => import('./components/accounts/accounts.component').then(m => m.AccountsComponent) },
  { path: 'operations', canActivate: [authGuard], loadComponent: () => import('./components/operations/operations.component').then(m => m.OperationsComponent) },
  { path: 'chatbot',    canActivate: [authGuard], loadComponent: () => import('./components/chatbot/chatbot.component').then(m => m.ChatbotComponent) },
  { path: '**', redirectTo: 'dashboard' }
];
