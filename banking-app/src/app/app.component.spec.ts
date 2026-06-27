import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, describe, it, beforeEach } from '@jest/globals';

describe('AppComponent (Jest)', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppComponent,
        RouterTestingModule 
      ]
    });
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    expect(app).toBeTruthy();
  });

  it('should have title "banking-app"', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    expect(app.title).toBe('banking-app');
  });

  it('should render router-outlet', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    // Verifica que exista el router-outlet
    expect(compiled.querySelector('router-outlet')).not.toBeNull();
  });

});
