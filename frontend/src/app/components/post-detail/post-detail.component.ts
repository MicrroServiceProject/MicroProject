import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PostService } from '../../services/post.service';
import { NotificationService } from '../../services/notification.service';
import { Post } from '../../models/post.model';
import { BlogComment } from '../../models/comment.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.css']
})
export class PostDetailComponent implements OnInit {
  post$: Observable<Post | undefined>;
  newComment: BlogComment = { id: '', content: '', authorUsername: '', createdAt: '' };
  defaultImage = 'https://placehold.co/800x400?text=Image+non+disponible';
  isSubmitting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private notificationService: NotificationService
  ) {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.post$ = this.postService.getPostById(id);
  }

  ngOnInit(): void {}

  toggleLike(postId: string): void {
    this.postService.toggleLike(postId);
  }

  toggleFavorite(postId: string): void {
    this.postService.toggleFavorite(postId);
  }

  async addComment(postId: string): Promise<void> {
    if (this.newComment.content && this.newComment.authorUsername && !this.isSubmitting) {
      this.isSubmitting = true;
      try {
        const commentToSubmit: BlogComment = {
          ...this.newComment,
          createdAt: new Date().toISOString()
        };
        await this.postService.addComment(postId, commentToSubmit).toPromise();

        // Notify the admin about the new comment
        this.post$.subscribe(post => {
          if (post) {
            const notificationMessage = `Nouveau commentaire sur l'article "${post.title}" par ${this.newComment.authorUsername}: ${this.newComment.content}`;
            this.postService.notifyAdminOfComment(post.id, notificationMessage).subscribe({
              next: () => console.log('Admin notified of new comment'),
              error: (err: any) => console.error('Error notifying admin:', err) // Explicitly type err as any
            });
          }
        });

        this.newComment = { id: '', content: '', authorUsername: '', createdAt: '' };
      } catch (error) {
        console.error('Erreur lors de l\'ajout du commentaire:', error);
      } finally {
        this.isSubmitting = false;
      }
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  getImageUrl(imageUrl: string | undefined): string {
    return imageUrl || this.defaultImage;
  }

  trackById(index: number, comment: BlogComment): string {
    return comment.id;
  }

  formatCategory(category: string | undefined): string {
    if (!category) return 'Sans catégorie';
    return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
  }
}