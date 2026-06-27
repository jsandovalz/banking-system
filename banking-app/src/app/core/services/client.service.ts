import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private endpoint = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) { }
  
  list(): Observable<Client[]> {
    return this.http.get<Client[]>(this.endpoint);
  }

  get(clientId: string): Observable<Client> {
    return this.http.get<Client>(`${this.endpoint}/${clientId}`);
  }

  create(dto: Client): Observable<Client> {
    return this.http.post<Client>(this.endpoint, dto);
  }

  update(clientId: string, dto: Client): Observable<Client> {
    return this.http.put<Client>(`${this.endpoint}/${clientId}`, dto);
  }

  delete(clientId: string): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${clientId}`);
  }
}
