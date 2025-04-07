import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent implements OnInit {
  favorites$: Observable<Post[]>;
  defaultImage = 'https://placehold.co/300x200?text=Image+non+disponible'; // Updated URL

  constructor(private postService: PostService) {
    this.favorites$ = this.postService.getFavorites();
  }

  ngOnInit(): void {}

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