import { Component, signal } from '@angular/core';
import { ReportService } from '../../core/services/report.service';
import { ResponseReport } from '../../core/models/responseReport.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent {

  clientId = '';
  from = '';
  to = '';
  data = signal<ResponseReport | null>(null);
  error = signal('');
  ok = signal('');
  
  constructor(private reportService: ReportService) {}

  generate(): void {
    if (!this.clientId || !this.from || !this.to) {
      this.error.set('Complete los campos'); return;
    }
    this.error.set('');
    this.reportService.generate(this.clientId, this.from, this.to).subscribe({
      next: d => {this.data.set(d)},
      error: e => this.error.set(e.displayMessage || 'Error generando reporte')
    });
  }

  downloadPdf(): void {
    this.reportService.downloadPdf(this.clientId, this.from, this.to).subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url; a.download = `estado-cuenta-${this.clientId}.pdf`;
        a.click(); URL.revokeObjectURL(url);
      },
      error: e => this.error.set(e.displayMessage || 'Error descargando PDF')
    });
  }

}
