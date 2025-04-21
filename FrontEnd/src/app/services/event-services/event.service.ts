import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event } from '../../models/events.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = `${environment.apiUrl}/api/evenements`;

  constructor(private http: HttpClient) {}

  // Get all events
  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }

  // Get upcoming events
  getUpcomingEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/avenir`);
  }

  // Get event by ID
  getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`);
  }

  // Create event
  createEvent(event: Event, artisteId: string): Observable<Event> {
    return this.http.post<Event>(`${environment.apiUrl}/api/artistes/${artisteId}/evenements`, this.mapToBackendEvent(event));
  }

  // Update event
  updateEvent(event: Event): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${event.id}`, this.mapToBackendEvent(event));
  }

  // Delete event
  deleteEvent(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Search events
  searchEvents(params: {
    nom?: string,
    dateDebut?: Date,
    dateFin?: Date,
    artisteId?: string,
    capaciteMin?: number
  }): Observable<Event[]> {
    let httpParams = new HttpParams();
    
    if (params.nom) httpParams = httpParams.set('nom', params.nom);
    if (params.dateDebut) httpParams = httpParams.set('dateDebut', params.dateDebut.toISOString().split('T')[0]);
    if (params.dateFin) httpParams = httpParams.set('dateFin', params.dateFin.toISOString().split('T')[0]);
    if (params.artisteId) httpParams = httpParams.set('artisteId', params.artisteId);
    if (params.capaciteMin) httpParams = httpParams.set('capaciteMin', params.capaciteMin.toString());

    return this.http.get<Event[]>(`${this.apiUrl}/recherche`, { params: httpParams });
  }

  // Helper method to map frontend model to backend model
  private mapToBackendEvent(event: Event): any {
    return {
      id: event.id,
      nom: event.nom,
      description: event.description,
      date: event.date ? new Date(event.date).toISOString() : null,
      capaciteMax: event.capaciteMax,
      location: event.location,
      artiste: event.artisteId ? { id: event.artisteId } : null
    };
  }
}
