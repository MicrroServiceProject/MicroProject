import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { User } from '../models/user.model';

@Component({
  selector: 'app-acceuil',
  templateUrl: './acceuil.component.html',
  styleUrls: ['./acceuil.component.css']
})
export class AcceuilComponent implements OnInit {
  isLoggedIn: boolean = false;
  currentUser: User | null = null;
  featuredEvents = [
    {
      id: 1,
      title: 'Summer Art Exhibition',
      date: '2023-07-15',
      image: 'assets/images/SummerArtExhibition.jpg',
      description: 'Explore the vibrant colors and emotions of summer through various artistic expressions.'
    },
    {
      id: 2,
      title: 'Sculpture Workshop',
      date: '2023-07-22',
      image: 'assets/images/SculptureWorkshop.jpg',
      description: 'Learn the fundamentals of sculpture from renowned artists in this hands-on workshop.'
    },
    {
      id: 3,
      title: 'Photography Masterclass',
      date: '2023-07-29',
      image: 'assets/images/PhotographyMasterclass.jpg',
      description: 'Capture the essence of art through your lens with professional guidance.'
    }
  ];
  
  featuredArtworks = [
    {
      id: 1,
      title: 'Abstract Harmony',
      artist: 'Elena Mikhailov',
      image: 'assets/images/artwork_1.jpg',
      medium: 'Oil on Canvas'
    },
    {
      id: 2,
      title: 'Urban Reflections',
      artist: 'Marcus Chen',
      image: 'assets/images/artwork_2.jpg',
      medium: 'Mixed Media'
    },
    {
      id: 3,
      title: 'Serenity in Blue',
      artist: 'Sophia Williams',
      image: 'assets/images/artwork_3.jpg',
      medium: 'Acrylic on Canvas'
    },
    {
      id: 4,
      title: 'Whispers of Nature',
      artist: 'James Rodriguez',
      image: 'assets/images/artwork_4.jpg',
      medium: 'Watercolor'
    }
  ];

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is logged in
    this.isLoggedIn = this.userService.isLoggedIn();
    
    // Get current user if logged in
    if (this.isLoggedIn) {
      this.currentUser = this.userService.getCurrentUser();
    }

    // Subscribe to user changes for dynamic updates
    this.userService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
    });
  }

  navigateToEvent(eventId: number): void {
    this.router.navigate(['/events', eventId]);
  }
  
  navigateToArtwork(artworkId: number): void {
    this.router.navigate(['/artworks', artworkId]);
  }

  // Logout function
  logout(): void {
    this.userService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Logout failed:', error);
        // Still clear local data and redirect even if API call fails
        localStorage.removeItem('token');
        localStorage.removeItem('currentUser');
        this.router.navigate(['/login']);
      }
    });
  }
}
