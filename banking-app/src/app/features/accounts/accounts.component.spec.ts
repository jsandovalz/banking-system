import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountsComponent } from './accounts.component';
import { AccountService } from '../../core/services/account.service';
import { of, throwError } from 'rxjs';
import { expect, jest, describe, it, beforeEach } from '@jest/globals';
import { Account } from '../../core/models/account.model';

describe('AccountsPageComponent', () => {
  let component: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;
  let mockSvc: jest.Mocked<AccountService>;

  const sample: Account[] = [
    { id: 1, accountNumber: '478758', accountType: 'AHORRO', initialBalance: 2000,
      availableBalance: 2000, status: true, clientId: 'joselema', nameClient: 'Jose Lema' },
    { id: 2, accountNumber: '225487', accountType: 'CORRIENTE', initialBalance: 100,
      availableBalance: 100, status: true, clientId: 'marianela', nameClient: 'Marianela Montalvo' }
  ];

  beforeEach(async () => {
    mockSvc = {
      list: jest.fn().mockReturnValue(of(sample)),
      get: jest.fn(),
      create: jest.fn().mockReturnValue(of(sample[0])),
      update: jest.fn().mockReturnValue(of(sample[0])),
      delete: jest.fn().mockReturnValue(of(void 0))
    } as unknown as jest.Mocked<AccountService>;


    await TestBed.configureTestingModule({
      imports: [AccountsComponent],
      providers: [{ provide: AccountService, useValue: mockSvc }]
    }).compileComponents();
    
    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and load accounts on ngOnInit', () => {
    expect(component).toBeTruthy();
    expect(mockSvc.list).toHaveBeenCalled();
    expect(component.accounts().length).toBe(2);
  });

  it('filters() should filter by account number', () => {
    component.filter = '478';
    expect(component.onFilters()).toHaveLength(1);
    expect(component.onFilters()[0].accountNumber).toBe('478758');
  });

  it('filters() should filter by customer name', () => {
    component.filter = 'marianela';
    expect(component.onFilters()[0].accountNumber).toBe('225487');
  });

  it('new() should initialize form with AHORRO as default type', () => {
    component.newAccount();
    expect(component.form()!.accountType).toBe('AHORRO');
    expect(component.form()!.status).toBe(true);
    expect(component.form()!.initialBalance).toBe(0);
  });

  it('save() should call create when ID is missing', () => {
    component.newAccount();
    component.form()!.accountNumber = '999999';
    component.form()!.clientId = 'joselema';
    component.onSave();
    expect(mockSvc.create).toHaveBeenCalled();
  });

  it('save() should call update when ID is present', () => {
    component.editAccount(sample[0]);
    component.onSave();
    expect(mockSvc.update).toHaveBeenCalledWith('478758', expect.any(Object));
  });

  it('delete() should call delete only when confirmed', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);
    component.onDelete(sample[0]);
    expect(mockSvc.delete).toHaveBeenCalledWith('478758');
  });

  it('should display an error message if save fails', () => {
    mockSvc.create.mockReturnValueOnce(throwError(() => ({ displayMessage: '' })));
    component.newAccount();
    component.onSave();
    expect(component.error()).toBe('duplicada');
  });

  it('should render two rows in the table', () => {
    const filas = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(filas.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('AHORRO');
    expect(fixture.nativeElement.textContent).toContain('CORRIENTE');
  });
  
});
