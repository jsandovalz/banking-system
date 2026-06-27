import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { expect, describe, it, beforeEach } from '@jest/globals';
import { apiErrorInterceptor } from './api-error.interceptor';

describe('ApiErrorInterceptor (Jest)', () => {

  let http: HttpTestingController;
  let client: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([apiErrorInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpTestingController);
    client = TestBed.inject(HttpClient);
  });

  it('should pass through successful requests', () => {
    client.get('/test').subscribe(res => {
      expect(res).toEqual({ ok: true });
    });

    const req = http.expectOne('/test');
    req.flush({ ok: true });
  });

  it('should handle ApiError structure', () => {
    const apiError = {
      code: 'BALANCE_NOT_FOUND',
      message: 'Balance not found',
      status: 404,
      details: null
    };

    client.get('/test').subscribe({
      next: () => fail('Should not succeed'),
      error: err => {
        expect(err.error.code).toBe('BALANCE_NOT_FOUND');
      }
    });

    const req = http.expectOne('/test');
    req.flush(apiError, { status: 404, statusText: 'Not Found' });
  });

});
function fail(arg0: string): void {
  throw new Error('Function not implemented.');
}

