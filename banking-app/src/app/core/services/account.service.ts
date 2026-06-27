import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Account } from '../models/account.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private endpoint = `${environment.apiUrl}/cuentas`;

  constructor(private http: HttpClient) { }

  list(clientId?:string): Observable<Account[]> {
    
    let params = new HttpParams();
    if(clientId) params = params.set('clientId',clientId);
    
    return this.http.get<Account[]>(this.endpoint,{params});
  }
  
  get(accountNumber: string): Observable<Account> {
    return this.http.get<Account>(`${this.endpoint}/${accountNumber}`);
  }

  create(dto: Account): Observable<Account> {
    return this.http.post<Account>(this.endpoint, dto);
  }

  update(accountNumber: string, dto: Account): Observable<Account> {
    return this.http.put<Account>(`${this.endpoint}/${accountNumber}`, dto);
  }

  delete(accountNumber: string): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${accountNumber}`);
  }
}
