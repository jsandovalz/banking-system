import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransactionService } from './transaction.service';
import { Transaction } from '../models/transaction.model';
import { environment } from '../../environments/environment';
import { expect, describe, it, beforeEach } from '@jest/globals';

describe('TransactionService (Jest)', () => {

  let service: TransactionService;
  let http: HttpTestingController;

  const endpoint = `${environment.apiUrl}/movimientos`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransactionService]
    });

    service = TestBed.inject(TransactionService);
    http = TestBed.inject(HttpTestingController);
  });

  // ---------------------------------------------------------
  // LIST
  // ---------------------------------------------------------
  it('should list all transactions (no filter)', () => {
    const mock: Transaction[] = [
      {
        id: 1,
        date: '2026-01-01',
        accountNumber: '12345',
        transactionType: 'DEPOSITO',
        amount: 100,
        balance: 200
      }
    ];

    service.list().subscribe(res => {
      expect(res).toEqual(mock);
    });

    const req = http.expectOne(r => r.url === endpoint && !r.params.has('accountNumber'));
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('should list transactions filtered by accountNumber', () => {
    const mock: Transaction[] = [];

    service.list('12345').subscribe(res => {
      expect(res).toEqual(mock);
    });

    const req = http.expectOne(r =>
      r.url === endpoint && r.params.get('accountNumber') === '12345'
    );

    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  // ---------------------------------------------------------
  // REGISTER
  // ---------------------------------------------------------
  it('should register a transaction', () => {
    const payload: Transaction = {
      id: 0,
      date: '2026-01-01',
      accountNumber: '12345',
      transactionType: 'DEPOSITO',
      amount: 100,
      balance: 200
    };

    const mockResponse: Transaction = { ...payload, id: 99 };

    service.register(payload).subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = http.expectOne(endpoint);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush(mockResponse);
  });

  // ---------------------------------------------------------
  // DELETE
  // ---------------------------------------------------------
  it('should delete a transaction', () => {
    service.delete(10).subscribe(res => {
      expect(res).toBeUndefined();
    });

    const req = http.expectOne(`${endpoint}/10`);
    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });

});
