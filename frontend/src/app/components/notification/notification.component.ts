import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
  notifications$: Observable<Notification[]>;
  notificationCount$: Observable<number>;

  constructor(private notificationService: NotificationService) {
    this.notifications$ = this.notificationService.getNotifications();
    this.notificationCount$ = this.notificationService.getNotificationCount();
  }

  ngOnInit(): void {}

  markAsRead(id: string): void {
    this.notificationService.markAsRead(id).subscribe();
  }

  trackById(index: number, notification: Notification): string {
    return notification.id;
  }
}