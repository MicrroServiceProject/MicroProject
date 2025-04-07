import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http'; // Ajout pour HttpClient
export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes),
    provideHttpClient() // Ajout pour activer HttpClient
  ]
};