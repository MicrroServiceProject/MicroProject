import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { Post } from '../models/post.model';
import { BlogComment } from '../models/comment.model';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080/api';
  private userApiUrl = `${this.apiUrl}/users`;
  private postsApiUrl = `${this.apiUrl}/posts`;
  private posts: Post[] = [];
  private postsSubject = new BehaviorSubject<Post[]>([]);
  private favoritesSubject = new BehaviorSubject<Post[]>([]);
  private searchQuerySubject = new BehaviorSubject<string>('');
  public favoritesCount$ = this.favoritesSubject.asObservable().pipe(
    map(favorites => favorites.length)
  );

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {
    this.loadPosts();
  }

  private loadPosts(): void {
    this.http.get<Post[]>(this.postsApiUrl).pipe(
      tap(posts => {
        this.posts = posts;
        this.postsSubject.next(posts);
        this.updateFavorites();
      }),
      catchError(error => {
        console.error('Erreur lors du chargement des posts:', error);
        return throwError(() => new Error('Impossible de charger les posts.'));
      })
    ).subscribe();
  }

  getPosts(): Observable<Post[]> {
    return this.postsSubject.asObservable();
  }

  getFavorites(): Observable<Post[]> {
    return this.favoritesSubject.asObservable();
  }

  getPostById(id: string): Observable<Post | undefined> {
    return this.http.get<Post>(`${this.postsApiUrl}/${id}`).pipe(
      catchError(error => {
        console.error(`Erreur lors de la récupération du post ${id}:`, error);
        return throwError(() => new Error('Post non trouvé.'));
      })
    );
  }

  addPost(post: Post): Observable<Post> {
    console.log('Adding post for user:', post.authorUsername);
    return this.http.get<any>(`${this.userApiUrl}/username/${encodeURIComponent(post.authorUsername)}`).pipe(
      switchMap(user => {
        if (!user || !user.id) {
          throw new Error('Utilisateur non trouvé pour le nom d\'utilisateur: ' + post.authorUsername);
        }
        post.authorId = user.id;
        return this.http.post<Post>(this.postsApiUrl, post);
      }),
      tap(newPost => {
        this.posts.push(newPost);
        this.postsSubject.next([...this.posts]);
        this.updateFavorites();
        const notification = {
          id: '',
          message: `Nouvel article créé : ${newPost.title}`,
          createdAt: new Date().toISOString(),
          read: false
        };
        this.notificationService.addNotification(notification).subscribe();
      }),
      catchError(error => {
        console.error('Erreur lors de l\'ajout du post:', error);
        let errorMessage = 'Erreur serveur.';
        if (error.status === 404) {
          errorMessage = `Utilisateur "${post.authorUsername}" non trouvé. Veuillez vérifier le nom d'utilisateur.`;
        } else if (error.message) {
          errorMessage = error.message;
        }
        return throwError(() => new Error(`Impossible d'ajouter le post: ${errorMessage}`));
      })
    );
  }

  updatePost(id: string, post: Post): Observable<Post> {
    return this.http.put<Post>(`${this.postsApiUrl}/${id}`, post).pipe(
      tap(updatedPost => {
        const index = this.posts.findIndex(p => p.id === id);
        if (index !== -1) {
          this.posts[index] = updatedPost;
          this.postsSubject.next([...this.posts]);
          this.updateFavorites();
        }
      }),
      catchError(error => {
        console.error('Erreur lors de la mise à jour du post:', error);
        return throwError(() => new Error('Impossible de mettre à jour le post.'));
      })
    );
  }

  deletePost(id: string): Observable<void> {
    return this.http.delete<void>(`${this.postsApiUrl}/${id}`).pipe(
      tap(() => {
        this.posts = this.posts.filter(post => post.id !== id);
        this.postsSubject.next([...this.posts]);
        this.updateFavorites();
      }),
      catchError(error => {
        console.error('Erreur lors de la suppression du post:', error);
        return throwError(() => new Error('Impossible de supprimer le post.'));
      })
    );
  }

  toggleLike(postId: string): void {
    this.http.post<void>(`${this.postsApiUrl}/${postId}/like`, {}).pipe(
      tap(() => {
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          post.isLiked = !post.isLiked;
          post.likes = post.isLiked ? post.likes + 1 : post.likes - 1;
          this.postsSubject.next([...this.posts]);
          this.updateFavorites();
        }
      })
    ).subscribe();
  }

  toggleFavorite(postId: string): void {
    this.http.post<void>(`${this.postsApiUrl}/${postId}/favorite`, {}).pipe(
      tap(() => {
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          post.isFavorite = !post.isFavorite;
          this.postsSubject.next([...this.posts]);
          this.updateFavorites();
        }
      })
    ).subscribe();
  }

  addComment(postId: string, comment: BlogComment): Observable<BlogComment> {
    console.log('Adding comment for user:', comment.authorUsername);
    console.log('Requesting user at:', `${this.userApiUrl}/username/${encodeURIComponent(comment.authorUsername)}`);
    return this.http.get<any>(`${this.userApiUrl}/username/${encodeURIComponent(comment.authorUsername)}`).pipe(
      switchMap(user => {
        console.log('User response:', user);
        if (!user || !user.id) {
          throw new Error(`Utilisateur "${comment.authorUsername}" non trouvé. Veuillez vérifier le nom d'utilisateur.`);
        }
        const commentPayload = {
          content: comment.content,
          postId: postId,
          authorId: user.id,
          authorUsername: comment.authorUsername,
          createdAt: comment.createdAt || new Date().toISOString()
        };
        console.log('Posting comment to:', `${this.apiUrl}/comments`, commentPayload);
        return this.http.post<BlogComment>(`${this.apiUrl}/comments`, commentPayload);
      }),
      tap(newComment => {
        console.log('New comment added:', newComment);
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          if (!post.comments) {
            post.comments = [];
          }
          post.comments.push(newComment);
          this.postsSubject.next([...this.posts]);
        }
        const message = `Nouveau commentaire ajouté sur le post ${postId} par ${comment.authorUsername}`;
        this.notifyAdminOfComment(postId, message).subscribe({
          error: (err) => {
            console.error('Error notifying admin:', err);
            // Continue even if notification fails
          }
        });
      }),
      catchError(error => {
        console.error('Erreur lors de l\'ajout du commentaire:', error);
        let errorMessage = 'Erreur serveur lors de l\'ajout du commentaire.';
        if (error.status === 404) {
          errorMessage = `Utilisateur "${comment.authorUsername}" non trouvé ou endpoint non disponible.`;
        } else if (error.status === 400) {
          errorMessage = error.error || 'Requête invalide.';
        } else if (error.status === 500) {
          errorMessage = error.error || 'Erreur serveur interne.';
        } else if (error.message) {
          errorMessage = error.message;
        }
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  setSearchQuery(query: string): void {
    this.searchQuerySubject.next(query);
    if (query.trim() === '') {
      this.postsSubject.next([...this.posts]);
    } else {
      this.http.get<Post[]>(`${this.postsApiUrl}/search?query=${encodeURIComponent(query)}`).pipe(
        tap(filteredPosts => {
          this.postsSubject.next(filteredPosts);
        })
      ).subscribe();
    }
  }

  private updateFavorites(): void {
    const favorites = this.posts.filter(post => post.isFavorite);
    this.favoritesSubject.next(favorites);
  }

  notifyAdminOfComment(postId: string, message: string): Observable<void> {
    console.log('Notifying admin:', { postId, message });
    return this.http.post<void>(`${this.apiUrl}/notify-admin`, { postId, message }).pipe(
      catchError(error => {
        console.error('Erreur lors de la notification de l\'admin:', error);
        let errorMessage = 'Impossible de notifier l\'admin.';
        if (error.status === 500) {
          errorMessage = error.error || 'Erreur serveur interne lors de la notification.';
        }
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}