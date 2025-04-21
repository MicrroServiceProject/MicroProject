import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation, StatutReservation, Evenement } from '../models/Reservation';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) { }

  // Créer une réservation
  createReservation(reservation: Reservation, clientId: string, evenementId: string): Observable<Reservation> {
    return this.http.post<Reservation>(
      `${this.apiUrl}/clients/${clientId}/reservations/evenements/${evenementId}`,
      reservation
    );
  }

  // Obtenir toutes les réservations
  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/reservations`);
  }

  // Obtenir une réservation par ID
  getReservationById(id: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/reservations/${id}`);
  }

  // Mettre à jour une réservation
  updateReservation(id: string, reservation: Reservation): Observable<Reservation> {
    return this.http.put<Reservation>(
      `${this.apiUrl}/reservations/${id}`,
      reservation
    );
  }

  // Modifier le statut d'une réservation
  updateReservationStatus(id: string, statut: StatutReservation): Observable<Reservation> {
    return this.http.put<Reservation>(
      `${this.apiUrl}/reservations/${id}/statut`,
      {},
      { params: { statut } }
    );
  }

  // Annuler une réservation
  cancelReservation(id: string): Observable<Reservation> {
    return this.http.post<Reservation>(
      `${this.apiUrl}/reservations/${id}/annulation`,
      {}
    );
  }

  // Supprimer une réservation
  deleteReservation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/reservations/${id}`);
  }

  // Obtenir les réservations d'un client
  getReservationsByClient(clientId: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/clients/${clientId}/reservations`);
  }

  // Obtenir les réservations d'un événement
  getReservationsByEvent(evenementId: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/evenements/${evenementId}/reservations`);
  }

  // Vérifier la disponibilité pour un événement
  checkEventAvailability(evenementId: string, placesRequested: number): Observable<{ available: boolean, message?: string }> {
    return this.http.get<{ available: boolean, message?: string }>(
      `${this.apiUrl}/evenements/${evenementId}/availability`,
      { params: { places: placesRequested.toString() } }
    );
  }
}