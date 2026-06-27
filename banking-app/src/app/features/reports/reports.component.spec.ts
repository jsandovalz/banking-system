import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReportsComponent } from './reports.component';
import { ReportService } from '../../core/services/report.service';
import { of, throwError } from 'rxjs';
import { expect, jest, describe, it, beforeEach } from '@jest/globals';
import { ResponseReport } from '../../core/models/responseReport.model';

describe('ReportsPageComponent', () => {
  let component: ReportsComponent;
  let fixture: ComponentFixture<ReportsComponent>;
  let mockSvc: jest.Mocked<ReportService>;

  const mockResp: ResponseReport = {
    report: {
      client: 'Marianela Montalvo',
      from: '2022-02-01',
      to: '2022-02-28',
      totalCredits: 600,
      totalDebits: 540,
      transactions: [
        { date: '2022-02-10T10:00:00', client: 'Marianela Montalvo',
          accountNumber: '225487', accountType: 'CORRIENTE', initialBalance: 100, status: true,
          transactionType: 'DEPOSITO', amount: 600, balance: 700 }
      ]
    },
    pdfBase64: 'JVBERi0xLjQK'
  };

  beforeEach(async () => {
    mockSvc = {
      generate: jest.fn().mockReturnValue(of(mockResp)),
      downloadPdf: jest.fn().mockReturnValue(of(new Blob(['%PDF'], { type: 'application/pdf' })))
    } as unknown as jest.Mocked<ReportService>;

    await TestBed.configureTestingModule({
      imports: [ReportsComponent],
      providers: [{ provide: ReportService, useValue: mockSvc }]
    }).compileComponents();
    
    fixture = TestBed.createComponent(ReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('generate() should validate that all fields are required', () => {
    component.generate();
    expect(component.error()).toBe('Complete los campos');
    expect(mockSvc.generate).not.toHaveBeenCalled();
  });

  it('generate() should call the service and store the response', () => {
    component.clientId = 'marianela';
    component.from = '2022-02-01';
    component.to = '2022-02-28';
    component.generate();
    expect(mockSvc.generate).toHaveBeenCalledWith('marianela', '2022-02-01', '2022-02-28');
    expect(component.data()!.report.client).toBe('Marianela Montalvo');
  });

  it('generte() should display an error if the service fails', () => {
    mockSvc.generate.mockReturnValueOnce(throwError(() => ({ displayMessage: 'no encontrado' })));
    component.clientId = 'x';
    component.from = '2022-01-01';
    component.to = '2022-12-31';
    component.generate();
    expect(component.error()).toBe('no encontrado');
  });

  it('downloadPdf() should create a blob and trigger the download', () => {
    const createObjectURL = jest.fn().mockReturnValue('blob:mock');
    const revokeObjectURL = jest.fn();
    Object.defineProperty(URL, 'createObjectURL', { value: createObjectURL });
    Object.defineProperty(URL, 'revokeObjectURL', { value: revokeObjectURL });
    const click = jest.fn();
    jest.spyOn(document, 'createElement').mockReturnValueOnce({ click, set href(_v: string) {}, set download(_v: string) {} } as any);

    component.clientId = 'joselema';
    component.from = '2022-01-01';
    component.to = '2022-12-31';
    component.downloadPdf();

    expect(mockSvc.downloadPdf).toHaveBeenCalled();
    expect(createObjectURL).toHaveBeenCalled();
    expect(click).toHaveBeenCalled();
    expect(revokeObjectURL).toHaveBeenCalled();
  });

  it('should render the report table when data is provided', () => {
    component.clientId = 'marianela';
    component.from = '2022-02-01';
    component.to = '2022-02-28';
    component.generate();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Marianela Montalvo');
    expect(fixture.nativeElement.textContent).toContain('225487');
    const filas = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(filas.length).toBe(1);
  });
});
