import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Notification } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/api/notifications';
  private notifications: Notification[] = [];
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  private notificationCountSubject = new BehaviorSubject<number>(0);

  constructor(private http: HttpClient) {
    this.loadNotifications();
  }

  private loadNotifications(): void {
    this.http.get<Notification[]>(this.apiUrl).pipe(
      tap(notifications => {
        this.notifications = notifications || [];
        this.notificationsSubject.next(this.notifications);
        this.updateNotificationCount();
      }),
      catchError(error => {
        console.error('Erreur lors du chargement des notifications:', error);
        this.notifications = [];
        this.notificationsSubject.next(this.notifications);
        this.updateNotificationCount();
        return throwError(() => new Error('Impossible de charger les notifications.'));
      })
    ).subscribe();
  }

  getNotifications(): Observable<Notification[]> {
    return this.notificationsSubject.asObservable();
  }

  getNotificationCount(): Observable<number> {
    return this.notificationCountSubject.asObservable();
  }

  addNotification(notification: Notification): Observable<Notification> {
    return this.http.post<Notification>(this.apiUrl, notification).pipe(
      tap(newNotification => {
        this.notifications.push(newNotification);
        this.notificationsSubject.next([...this.notifications]);
        this.updateNotificationCount();
      }),
      catchError(error => {
        console.error('Erreur lors de l\'ajout de la notification:', error);
        return throwError(() => new Error('Impossible d\'ajouter la notification.'));
      })
    );
  }

  markAsRead(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {}).pipe(
      tap(() => {
        const notification = this.notifications.find(n => n.id === id);
        if (notification) {
          notification.read = true;
          this.notificationsSubject.next([...this.notifications]);
          this.updateNotificationCount();
        }
      }),
      catchError(error => {
        console.error('Erreur lors de la mise à jour de la notification:', error);
        return throwError(() => new Error('Impossible de marquer la notification comme lue.'));
      })
    );
  }

  private updateNotificationCount(): void {
    const unreadCount = (this.notifications || []).filter(n => !n.read).length;
    this.notificationCountSubject.next(unreadCount);
  }
}