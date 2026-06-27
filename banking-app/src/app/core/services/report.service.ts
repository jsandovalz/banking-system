import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseReport } from '../models/responseReport.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  private endpoint = `${environment.apiUrl}/reportes`;

  constructor(private http: HttpClient) { }

  generate(clientId: string, from: string, to: string): Observable<ResponseReport> {
    const params = new HttpParams()
      .set('clientId', clientId)
      .set('from', from)
      .set('to', to);
    return this.http.get<ResponseReport>(this.endpoint, { params });
  }

  downloadPdf(clientId: string, from: string, to: string): Observable<Blob> {
    const params = new HttpParams()
      .set('clientId', clientId)
      .set('from', from)
      .set('to', to);
    return this.http.get(`${this.endpoint}/pdf`, { params, responseType: 'blob' });
  }
}
