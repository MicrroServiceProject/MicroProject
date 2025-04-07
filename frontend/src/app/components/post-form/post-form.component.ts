import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.css']
})
export class PostFormComponent implements OnInit {
  post: Post = {
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
  isEditMode = false;
  isSubmitting = false;
  errorMessage: string | null = null;
  categories = ['Peinture', 'Sculpture', 'Photographie', 'Art Numerique'];

  constructor(
    private postService: PostService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.postService.getPostById(id).subscribe({
        next: (post) => {
          if (post) {
            this.post = post;
          }
        },
        error: (error) => {
          console.error('Erreur lors de la récupération du post:', error);
          this.errorMessage = 'Impossible de charger l\'article pour modification.';
        }
      });
    }
  }

  async onSubmit(): Promise<void> {
    this.isSubmitting = true;
    this.errorMessage = null;
    try {
      const postToSubmit: Post = {
        ...this.post,
        createdAt: this.isEditMode ? this.post.createdAt : new Date().toISOString(),
      };

      if (this.isEditMode) {
        await this.postService.updatePost(this.post.id, postToSubmit).toPromise();
      } else {
        await this.postService.addPost(postToSubmit).toPromise();
      }
      this.router.navigate(['/']);
    } catch (error: any) {
      console.error('Erreur lors de la soumission du formulaire:', error);
      this.errorMessage = error.message || 'Une erreur est survenue lors de la création de l\'article. Vérifiez que le serveur est en cours d\'exécution.';
    } finally {
      this.isSubmitting = false;
    }
  }

  cancel(): void {
    this.router.navigate(['/']);
  }
}