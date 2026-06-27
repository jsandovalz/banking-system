import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Account } from '../../core/models/account.model';
import { AccountService } from '../../core/services/account.service';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css'
})
export class AccountsComponent implements OnInit {

  accounts = signal<Account[]>([]);
  form = signal<Account | null>(null);
  error = signal<string>('');
  ok = signal<string>('');
  filter = '';

  loading = false;

  showForm = false;
  
  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.accountService.list().subscribe({
      next: (data) => {
        this.accounts.set(data);
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
  onFilters(): Account[] {
    const f = this.filter.toLowerCase();
    return this.accounts().filter(c => !f
      || c.accountNumber.includes(f) || c.clientId.toLowerCase().includes(f)
      || (c.accountType || '').toLowerCase().includes(f));
  }

  newAccount(): void {
    this.form.set({ accountNumber: '', accountType: 'AHORRO', initialBalance: 0, status: true, clientId: '' });
    this.showForm = true;
  }

  editAccount(account: Account): void {
    this.showForm = true;
    this.form.set({ ...account }); 
  }

  onSave(): void {
    const f = this.form()!;
    if (f.id) {
      this.accountService.update(f.accountNumber, f).subscribe({
        next: () => { this.ok.set('Guardado correctamente');this.form.set(null) ;this.showForm = false; this.loadAccounts();setTimeout(()=>this.ok.set(''),3000);  },
        error: e => this.error.set(e.displayMessage || 'duplicada')
      });
    } else {
      this.accountService.create(f).subscribe({
        next: () => { this.ok.set('Guardado correctamente');this.form.set(null) ; this.loadAccounts(); setTimeout(()=>this.ok.set(''),3000); },
        error: e => this.error.set(e.displayMessage || 'duplicada')
      });
    }
  }
  onCancel(): void { this.form.set(null); }

  onDelete(account: Account): void {
    if (!confirm('Eliminar Cuenta ' + account.accountNumber + '?')) return;
    this.accountService.delete(account.accountNumber).subscribe({
      next: () => this.loadAccounts(),
      error: e => this.error.set(e.displayMessage || 'Error al eliminar')
    });
  }

}
