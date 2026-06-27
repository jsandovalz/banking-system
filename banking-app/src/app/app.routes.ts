import { Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', redirectTo: 'clientes', pathMatch: 'full' },
    {
        path: 'clientes',
        loadComponent: () => import('./features/clients/clients.component').then(m => m.ClientsComponent)
    },
    {
        path: 'cuentas',
        loadComponent: () => import('./features/accounts/accounts.component').then(m => m.AccountsComponent)
    },
    {
        path: 'movimientos',
        loadComponent: () => import('./features/transactions/transactions.component').then(m => m.TransactionsComponent)
    },
    {
        path: 'reportes',
        loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent)
    }
];
