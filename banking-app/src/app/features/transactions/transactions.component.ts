import { Component, OnInit, signal } from '@angular/core';
import { TransactionService } from '../../core/services/transaction.service';
import { Transaction } from '../../core/models/transaction.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css'
})
export class TransactionsComponent implements OnInit{

  transactions = signal<Transaction[]>([]);
  form = signal<Transaction | null>(null);
  error = signal('');
  ok = signal('');
  filter = '';

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void { 
    this.load(); 
  }

  load(): void {
    this.transactionService.list().subscribe({
      next: d => this.transactions.set(d),
      error: e => this.error.set(e.displayMessage || 'Error')
    });
  }

  filters(): Transaction[] {
    const f = this.filter.toLowerCase();
    return this.transactions().filter(m => !f || m.accountNumber.includes(f));
  }

  new(): void { this.form.set({ accountNumber: '', transactionType: 'DEPOSITO', amount: 0 }); }
  cancel(): void { this.form.set(null); }

  save(): void {
    const m = this.form()!;
    this.transactionService.register(m).subscribe({
      next: () => { this.ok.set('Movimiento registrado'); this.form.set(null); this.load(); setTimeout(()=>this.ok.set(''),3000); },
      error: e => this.error.set(e.displayMessage || 'Error al registrar')
    });
  }

  delete(m: Transaction): void {
    if (!m.id || !confirm('Eliminar movimiento?')) return;
    this.transactionService.delete(m.id).subscribe({
      next: () => this.load(),
      error: e => this.error.set(e.displayMessage || 'Error al eliminar')
    });
  }

}
