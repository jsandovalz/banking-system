import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Transaction } from '../models/transaction.model';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private endpoint = `${environment.apiUrl}/movimientos`;

  constructor(private http: HttpClient) { }

  list(accountNumber?: string): Observable<Transaction[]> {
    let params = new HttpParams();
    if (accountNumber) params = params.set('accountNumber', accountNumber);
    return this.http.get<Transaction[]>(this.endpoint, { params });
  }
  register(transac: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(this.endpoint, transac);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
}
