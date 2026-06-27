import { TestBed } from '@angular/core/testing';
import { ClientService } from './client.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Client } from '../models/client.model';
import { expect, describe, it, beforeEach } from '@jest/globals';

describe('ClientService (Jest)', () => {

  let service: ClientService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClientService]
    });

    service = TestBed.inject(ClientService);
    http = TestBed.inject(HttpTestingController);
  });

  it('should list clients', () => {
    const mockClients: Client[] = [
      { name: 'Jose', gender: 'M', age: 30, identification: '123', address: 'X', phone: '999', clientId: 'jose', password: '123', status: true }
    ];

    service.list().subscribe(res => {
      expect(res).toEqual(mockClients);
    });

    const req = http.expectOne(r => r.url.endsWith('/clientes'));
    expect(req.request.method).toBe('GET');
    req.flush(mockClients);
  });

  it('should delete client', () => {
    service.delete('jose').subscribe(res => {
      expect(res).toEqual({});
    });

    const req = http.expectOne(r => r.url.endsWith('/clientes/jose'));
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

});
