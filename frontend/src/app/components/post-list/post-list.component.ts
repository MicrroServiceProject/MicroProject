import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit {
  posts$: Observable<Post[]>;
  defaultImage = 'https://placehold.co/300x200?text=Image+non+disponible'; // Updated URL
  isLoading = true;
  errorMessage: string | null = null;

  constructor(private postService: PostService) {
    this.posts$ = this.postService.getPosts().pipe(
      catchError((error: unknown) => {
        console.error('Erreur lors du chargement des articles:', error);
        this.errorMessage = 'Impossible de charger les articles. Vérifiez que le serveur est en cours d\'exécution.';
        this.isLoading = false;
        return of([]);
      })
    );
  }

  ngOnInit(): void {
    this.posts$.subscribe(() => {
      this.isLoading = false;
    });
  }

  toggleLike(postId: string): void {
    this.postService.toggleLike(postId);
  }

  toggleFavorite(postId: string): void {
    this.postService.toggleFavorite(postId);
  }

  trackById(index: number, post: Post): string {
    return post.id;
  }

  getImageUrl(imageUrl: string | undefined): string {
    return imageUrl || this.defaultImage;
  }

  formatCategory(category: string | undefined): string {
    if (!category) return 'Sans catégorie';
    return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
  }
}