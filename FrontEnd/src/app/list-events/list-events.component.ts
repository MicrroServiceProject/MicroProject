import { Component, OnInit } from '@angular/core';
import { EventService } from '../services/event-services/event.service';
import { Event } from '../models/events.model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservationService } from '../services/reservation.service';
import { UserService } from '../services/user.service';
import { Reservation, StatutReservation } from '../models/Reservation';

@Component({
  selector: 'app-event',
  templateUrl: './list-events.component.html',
  styleUrls: ['./list-events.component.css']
})
export class EventComponent implements OnInit {
  events: Event[] = [];
  filteredEvents: Event[] = [];
  searchForm: FormGroup;
  loading = true;
  error = false;
  selectedEvent: Event | null = null;
  reservationCount = 1;
  currentUserId: string | undefined;

  constructor(
    private eventService: EventService,
    private reservationService: ReservationService,
    private userService: UserService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      searchTerm: [''],
      dateStart: [null],
      dateEnd: [null]
    });
  }

  ngOnInit(): void {
    const currentUser = this.userService.getCurrentUser();
    this.currentUserId = currentUser?.userId?.toString();
    this.loadEvents();
    
    this.searchForm.valueChanges.subscribe(values => {
      this.filterEvents(values);
    });
  }

  loadEvents(): void {
    this.loading = true;
    this.eventService.getAllEvents().subscribe({
      next: (data) => {
        this.events = data;
        this.filteredEvents = [...data];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading events', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  filterEvents(values: any): void {
    let filtered = [...this.events];
    
    if (values.searchTerm) {
      const searchTerm = values.searchTerm.toLowerCase();
      filtered = filtered.filter(event => 
        event.nom?.toLowerCase().includes(searchTerm) || 
        event.description?.toLowerCase().includes(searchTerm) ||
        event.location?.toLowerCase().includes(searchTerm)
      );
    }
    
    if (values.dateStart) {
      filtered = filtered.filter(event => 
        new Date(event.date) >= new Date(values.dateStart)
      );
    }
    
    if (values.dateEnd) {
      filtered = filtered.filter(event => 
        new Date(event.date) <= new Date(values.dateEnd)
      );
    }
    
    this.filteredEvents = filtered;
  }

  openReservationModal(event: Event): void {
    if (!this.currentUserId) {
      this.router.navigate(['/login']);
      return;
    }
    this.selectedEvent = event;
    this.reservationCount = 1;
  }

  closeReservationModal(): void {
    this.selectedEvent = null;
    this.reservationCount = 1;
  }

  incrementReservation(): void {
    if (this.selectedEvent && this.reservationCount < this.getAvailableSeats(this.selectedEvent)) {
      this.reservationCount++;
    }
  }

  decrementReservation(): void {
    if (this.reservationCount > 1) {
      this.reservationCount--;
    }
  }

  getAvailableSeats(event: Event): number {
    return event.capaciteMax - (event.placesReservees || 0);
  }

  reserveEvent(): void {
    if (!this.selectedEvent || !this.currentUserId || !this.selectedEvent.id) return;
    
    const reservationData: Reservation = {
      nbrPlace: this.reservationCount,
      statut: StatutReservation.EN_COURS,
      evenementId: this.selectedEvent.id,
      clientId: this.currentUserId
    };

    this.reservationService.createReservation(
      reservationData, 
      this.currentUserId, 
      this.selectedEvent.id
    ).subscribe({
      next: (reservation) => {
        console.log('Reservation created successfully', reservation);
        if (this.selectedEvent) {
          this.selectedEvent.placesReservees = (this.selectedEvent.placesReservees || 0) + this.reservationCount;
          this.updateEventInList(this.selectedEvent);
        }
        this.closeReservationModal();
        alert('Réservation effectuée avec succès!');
      },
      error: (err) => {
        console.error('Error creating reservation', err);
        alert('Erreur lors de la réservation: ' + err.message);
      }
    });
  }

  private updateEventInList(updatedEvent: Event): void {
    const index = this.events.findIndex(e => e.id === updatedEvent.id);
    if (index !== -1) {
      this.events[index] = updatedEvent;
      this.filteredEvents = [...this.events];
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'Date non spécifiée';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  navigateToEventDetails(eventId: string): void {
    this.router.navigate(['/events', eventId]);
  }
}