import { TestBed } from '@angular/core/testing';
import { ClientsComponent } from './clients.component';
import { ClientService } from '../../core/services/client.service';
import { Client } from '../../core/models/client.model';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { expect, jest, describe, it, beforeEach } from '@jest/globals';

describe('ClientsComponent (Jest)', () => {

  let component: ClientsComponent;
  let service: jest.Mocked<ClientService>;

  const mockClients: Client[] = [
    {
      name: 'Jose Lema',
      gender: 'M',
      age: 30,
      identification: '0102030405',
      address: 'Av. Siempre Viva',
      phone: '098254785',
      clientId: 'joselema',
      password: '1234',
      status: true
    },
    {
      name: 'Marianela',
      gender: 'F',
      age: 28,
      identification: '0203040506',
      address: 'Calle Luna',
      phone: '097548965',
      clientId: 'marianela',
      password: 'abcd',
      status: true
    }
  ];

  beforeEach(() => {
    const mockService = {
      list: jest.fn(),
      delete: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [ClientsComponent, HttpClientTestingModule],
      providers: [{ provide: ClientService, useValue: mockService }]
    });

    service = TestBed.inject(ClientService) as jest.Mocked<ClientService>;
    component = TestBed.createComponent(ClientsComponent).componentInstance;
  });

  // ---------------------------------------------------------
  // INIT + LOAD CLIENTS
  // ---------------------------------------------------------
  it('should load clients on init', () => {
    service.list.mockReturnValue(of(mockClients));

    component.ngOnInit();

    expect(service.list).toHaveBeenCalled();
    expect(component.clients()).toEqual(mockClients);
    expect(component.loading).toBe(false);
  });

  // ---------------------------------------------------------
  // FILTERS
  // ---------------------------------------------------------
  it('should filter clients by name or clientId', () => {
    service.list.mockReturnValue(of(mockClients));
    component.ngOnInit();

    component.filter = 'jose';
    const result = component.onFilters();

    expect(result.length).toBe(1);
    expect(result[0].clientId).toBe('joselema');
  });

  // ---------------------------------------------------------
  // NEW CLIENT
  // ---------------------------------------------------------
  it('should create a new empty client form', () => {
    component.newClient();

    expect(component.showForm).toBe(true);
    expect(component.form()).toEqual({
      name: '',
      gender: 'M',
      age: 18,
      identification: '',
      address: '',
      phone: '',
      clientId: '',
      password: '',
      status: true
    });
  });

  // ---------------------------------------------------------
  // EDIT CLIENT
  // ---------------------------------------------------------
  it('should set form with selected client on edit', () => {
    const client = mockClients[0];

    component.editClient(client);

    expect(component.showForm).toBe(true);
    expect(component.form()).toEqual(client);
  });

  // ---------------------------------------------------------
  // CANCEL
  // ---------------------------------------------------------
  it('should clear form on cancel', () => {
    component.form.set(mockClients[0]);

    component.onCancel();

    expect(component.form()).toBeNull();
  });

  // ---------------------------------------------------------
  // SAVE
  // ---------------------------------------------------------
  it('should hide form and reload clients on save', () => {
    service.list.mockReturnValue(of(mockClients));

    component.showForm = true;
    component.onSave();

    expect(component.showForm).toBe(false);
    expect(service.list).toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // DELETE CONFIRMED
  // ---------------------------------------------------------
  it('should delete a client when confirmed', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);

    service.delete.mockReturnValue(of(void 0));
    service.list.mockReturnValue(of(mockClients));

    component.onDelete(mockClients[0]);

    expect(service.delete).toHaveBeenCalledWith('joselema');
    expect(service.list).toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // DELETE CANCELLED
  // ---------------------------------------------------------
  it('should NOT delete a client when cancel is pressed', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false);

    component.onDelete(mockClients[0]);

    expect(service.delete).not.toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // DELETE ERROR
  // ---------------------------------------------------------
  it('should set error message when delete fails', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);

    service.delete.mockReturnValue(
      throwError(() => ({ displayMessage: 'Error al eliminar' }))
    );

    component.onDelete(mockClients[0]);

    expect(component.error()).toBe('Error al eliminar');
  });

});
