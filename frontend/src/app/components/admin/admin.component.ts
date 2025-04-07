import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  posts$: Observable<Post[]>;
  defaultImage = 'https://placehold.co/300x200?text=Image+non+disponible';
  showForm: boolean = false;
  isLoading: boolean = false;
  newPost: Post = this.getEmptyPost();
  errorMessage: string | null = null;

  constructor(private postService: PostService) {
    this.posts$ = this.postService.getPosts().pipe(
      catchError((error: unknown) => {
        console.error('Erreur lors du chargement des articles:', error);
        this.errorMessage = 'Impossible de charger les articles. Vérifiez que le serveur est en cours d\'exécution.';
        return of([]);
      })
    );
  }

  ngOnInit(): void {}

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.resetForm();
    }
  }

  async addPost(): Promise<void> {
    if (this.isFormValid()) {
      try {
        this.isLoading = true;
        this.errorMessage = null;
        const postToSubmit: Post = {
          ...this.newPost,
          createdAt: new Date().toISOString(),
        };
        await this.postService.addPost(postToSubmit).toPromise();
        this.resetForm();
        this.showForm = false;
      } catch (error: any) {
        console.error('Erreur lors de l\'ajout:', error);
        this.errorMessage = error.message || 'Une erreur est survenue lors de l\'ajout. Vérifiez que le serveur est en cours d\'exécution.';
      } finally {
        this.isLoading = false;
      }
    }
  }

  resetForm(): void {
    this.newPost = this.getEmptyPost();
  }

  private getEmptyPost(): Post {
    return {
      id: '',
      title: '',
      content: '',
      imageUrl: '',
      authorUsername: 'khalil bannouri',
      authorId: '', // Add authorId to satisfy the Post interface
      createdAt: '',
      category: 'Peinture',
      likes: 0,
      isLiked: false,
      isFavorite: false,
      comments: [],
      views: 0
    };
  }

  private isFormValid(): boolean {
    return !!this.newPost.title && 
           !!this.newPost.content && 
           !!this.newPost.imageUrl && 
           !!this.newPost.authorUsername && 
           !!this.newPost.category;
  }

  trackById(index: number, post: Post): string {
    return post.id;
  }

  getImageUrl(imageUrl: string | undefined): string {
    return `url(${imageUrl || this.defaultImage})`;
  }

  async deletePost(id: string): Promise<void> {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet article ?')) {
      try {
        this.isLoading = true;
        this.errorMessage = null;
        await this.postService.deletePost(id).toPromise();
      } catch (error: any) {
        console.error('Erreur lors de la suppression:', error);
        this.errorMessage = error.message || 'Une erreur est survenue lors de la suppression. Vérifiez que le serveur est en cours d\'exécution.';
      } finally {
        this.isLoading = false;
      }
    }
  }
}