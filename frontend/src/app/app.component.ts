import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NotificationService } from './services/notification.service';
import { PostService } from './services/post.service';
import { Notification } from './models/notification.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, FormsModule, RouterModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  notificationCount$: Observable<number>;
  favoritesCount$: Observable<number>;
  notifications: Notification[] = [];
  showNotifications: boolean = false;
  searchQuery: string = '';
  isAdmin: boolean = false; // Hardcoded for now; replace with actual auth logic
  email: string = 'artblog@example.com';
  phone: string = '+123 456 7890';
  location: string = 'Paris, France';

  constructor(
    private notificationService: NotificationService,
    private postService: PostService,
    private router: Router
  ) {
    this.notificationCount$ = this.notificationService.getNotificationCount();
    this.favoritesCount$ = this.postService.favoritesCount$;
    this.notificationService.getNotifications().subscribe(notifications => {
      this.notifications = notifications;
    });
  }

  ngOnInit(): void {
    // Replace with actual authentication logic to determine if the user is an admin
    this.isAdmin = true; // For testing purposes, set to true to show the Admin link
    // In a real app, you might use a service like:
    // this.authService.isAdmin().subscribe(isAdmin => this.isAdmin = isAdmin);
  }

  onSearch(): void {
    this.postService.setSearchQuery(this.searchQuery);
  }

  onSearchFocus(): void {
    // Add logic if needed
  }

  onSearchBlur(): void {
    // Add logic if needed
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.postService.setSearchQuery('');
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  clearNotifications(): void {
    this.notifications = [];
    this.notificationService.getNotifications().subscribe(notifications => {
      this.notifications = notifications.filter(n => !n.read);
    });
  }

  viewPost(notification: Notification): void {
    const postTitle = notification.message.replace('Nouvel article créé : ', '');
    this.postService.getPosts().subscribe(posts => {
      const post = posts.find(p => p.title === postTitle);
      if (post) {
        this.router.navigate(['/post', post.id]);
        this.showNotifications = false;
      }
    });
  }

  trackById(index: number, notification: Notification): string {
    return notification.id;
  }
}