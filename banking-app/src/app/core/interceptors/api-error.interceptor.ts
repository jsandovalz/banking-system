import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const apiErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const body: any = error?.error ?? {};
      console.log("body:",body.details);
      
      let displayMessage: string;

      if (typeof body === 'string' && body.trim().length > 0) {
        displayMessage = body;
      } else if (body && Array.isArray(body.details) && body.details.length > 0) {
        displayMessage = body.details.join('\n');
      } else if (body && typeof body.message === 'string') {
        displayMessage = body.message;
      } else if (error?.statusText) {
        displayMessage = error.statusText;
      } else {
        displayMessage = 'Error de comunicación con el servidor';
      }

      const enhanced: any = error;
      enhanced.displayMessage = displayMessage;
      return throwError(() => enhanced);
    })
  );
};
