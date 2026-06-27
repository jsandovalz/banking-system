import { TestBed } from '@angular/core/testing';
import { AccountService } from './account.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect, describe, it, beforeEach } from '@jest/globals';

describe('AccountService (Jest)', () => {

  let service: AccountService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AccountService]
    });

    service = TestBed.inject(AccountService);
    http = TestBed.inject(HttpTestingController);
  });

  it('should list accounts', () => {
    const mock = [{ accountNumber: '123', accountType: 'AHORRO', initialBalance: 100, availableBalance: 100, status: true, clientId: 'jose', nameClient: 'Jose' }];

    service.list().subscribe(res => {
      expect(res.length > 0);
    });

    const req = http.expectOne(r => r.url.endsWith('/cuentas'));
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('should delete account', () => {
    service.delete('123').subscribe(res => {
      expect(res).toEqual({});
    });

    const req = http.expectOne(r => r.url.endsWith('/cuentas/123'));
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

});
