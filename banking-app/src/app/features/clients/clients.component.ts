import { Component, OnInit, signal } from '@angular/core';
import { ClientService } from '../../core/services/client.service';
import { Client } from '../../core/models/client.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-clients-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.scss']
})
export class ClientsComponent implements OnInit {

  clients = signal<Client[]>([]);
  form = signal<Client | null>(null);
  error = signal<string>('');
  ok = signal<string>('');
  filter = '';

  loading = false;

  showForm = false;

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.loading = true;
    this.clientService.list().subscribe({
      next: (data) => {
        this.clients.set(data);
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
  onFilters(): Client[] {
    const f = this.filter.toLowerCase();
    return this.clients().filter(c =>
      !f || c.name.toLowerCase().includes(f) || c.clientId.toLowerCase().includes(f) ||
      c.identification.includes(f) || c.phone.includes(f) );
  }

  newClient(): void {
    this.form.set({ name: '', gender: 'M', age: 18, identification: '',
      address: '', phone: '', clientId: '', password: '', status: true });
    this.showForm = true;
  }

  editClient(client: Client): void {
    this.showForm = true;
    this.form.set({ ...client }); 
  }

  onSave(): void {
    const c = this.form()!;
    const obs = c.id ? this.clientService.update(c.clientId, c) : this.clientService.create(c);
    obs.subscribe({
      next: () => { this.ok.set('Guardado correctamente'); this.form.set(null); this.loadClients(); setTimeout(()=>this.ok.set(''),3000); },
      error: e => {this.error.set(e.displayMessage || 'Error al guardar');setTimeout(()=>this.error.set(''),3000); }
    });
  }
  onCancel(): void { this.form.set(null); }

  onDelete(c: Client): void {
    if (!confirm('Eliminar cliente ' + c.clientId + '?')) return;
    this.clientService.delete(c.clientId).subscribe({
      next: () => this.loadClients(),
      error: e => this.error.set(e.displayMessage || 'Error al eliminar')
    });
  }
}
