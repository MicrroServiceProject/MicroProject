import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Artiste } from '../models/artiste.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ArtisteService {
  private apiUrl = `${environment.apiUrl}/api/artistes`;

  constructor(private http: HttpClient) {}

  getAllArtistes(): Observable<Artiste[]> {
    return this.http.get<Artiste[]>(this.apiUrl);
  }

  getArtisteById(id: string): Observable<Artiste> {
    return this.http.get<Artiste>(`${this.apiUrl}/${id}`);
  }
}