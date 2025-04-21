import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let token = localStorage.getItem('authToken'); // Get the token from localStorage
    const isPublicRequest = req.url.includes('/api/evenements'); // autoriser sans token

    console.log('Token in Interceptor:', token);  // Log token here

    if (token) {
      token = token.replace(/^Bearer\s+/i, '');
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`  // Add token to the Authorization header
        }
      });
      return next.handle(cloned);
    }

    return next.handle(req);
  }
}