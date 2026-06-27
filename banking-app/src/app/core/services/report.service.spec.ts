import { TestBed } from '@angular/core/testing';
import { ReportService } from './report.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ResponseReport } from '../models/responseReport.model';
import { expect, describe, it, beforeEach } from '@jest/globals';

describe('ReportService (Jest)', () => {

  let service: ReportService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReportService]
    });

    service = TestBed.inject(ReportService);
    http = TestBed.inject(HttpTestingController);
  });

  it('should generate report', () => {
    const mock: ResponseReport = {
      report: {
        client: 'Jose',
        from: '2026-01-01',
        to: '2026-06-01',
        totalCredits: 0,
        totalDebits: 100,
        transactions: []
      },
      pdfBase64: ''
    };

    service.generate('jose', '2026-01-01', '2026-06-01').subscribe(res => {
      expect(res).toEqual(mock);
    });

    const req = http.expectOne(r => r.urlWithParams.endsWith('/reports/resume?clientId=jose&from=2026-01-01&to=2026-06-01'));
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

});
