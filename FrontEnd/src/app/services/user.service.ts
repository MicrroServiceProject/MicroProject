import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { User, Role } from '../models/user.model';

interface AuthResponse {
  token: string;
  user: User;
  tokenType: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8093/api/users'; // Adjust to your Spring Boot endpoint
  
  private currentUserSubject: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private tokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(
    localStorage.getItem('token')
  );
  public token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is already logged in from localStorage
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  // Helper method to get auth headers
  private getAuthHeaders(): HttpHeaders {
    const token = this.tokenSubject.value;
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Get all users
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get user by ID
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Create a new user
  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }
// user.service.ts
getUserStatisticsByRole(): Observable<Map<string, number>> {
  return this.http.get<Map<string, number>>(`${this.apiUrl}/statistics/role`, {
    headers: this.getAuthHeaders()
  });
}

getTotalUsers(): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/statistics/total`, {
    headers: this.getAuthHeaders()
  });
}
  // Update a user
  updateUser(id: number, userDetails: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, userDetails, { headers: this.getAuthHeaders() })
      .pipe(
        tap(updatedUser => {
          // If the updated user is the current user, update the current user subject
          const currentUser = this.currentUserSubject.value;
          if (currentUser && currentUser.userId === updatedUser.userId) {
            this.currentUserSubject.next(updatedUser);
            localStorage.setItem('currentUser', JSON.stringify(updatedUser));
          }
        }),
        catchError(this.handleError)
      );
  }

  // Delete a user
  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Login
  login(email: string, password: string): Observable<AuthResponse> {
    const credentials = { email, password };
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          this.tokenSubject.next(response.token);
          this.currentUserSubject.next(response.user);
          
          localStorage.setItem('token', response.token);
          localStorage.setItem('currentUser', JSON.stringify(response.user));
        }),
        catchError(this.handleError)
      );
  }

  // Register
  register(user: User): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, user)
      .pipe(
        tap(response => {
          this.tokenSubject.next(response.token);
          this.currentUserSubject.next(response.user);
          
          localStorage.setItem('token', response.token);
          localStorage.setItem('currentUser', JSON.stringify(response.user));
        }),
        catchError(this.handleError)
      );
  }

  // Logout
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { headers: this.getAuthHeaders() })
      .pipe(
        tap(() => {
          // Clear stored user data
          this.tokenSubject.next(null);
          this.currentUserSubject.next(null);
          localStorage.removeItem('token');
          localStorage.removeItem('currentUser');
        }),
        catchError(this.handleError)
      );
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return !!this.tokenSubject.value;
  }

  // Get current user
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Check if user has specific role
  hasRole(role: Role): boolean {
    const user = this.currentUserSubject.value;
    return user !== null && user.role === role;
  }

  // Get current user role
  getCurrentUserRole(): Role | null {
    const currentUser = this.getCurrentUser();
    return currentUser ? currentUser.role : null;
  }

  // Error handling
  private handleError(error: any): Observable<never> {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
  // Ajoutez cette méthode à votre UserService
// Dans UserService
decodeToken(token: string): User | null {
  try {
    const payload = token.split('.')[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch (e) {
    console.error('Failed to decode token', e);
    return null;
  }
}

// Modifiez setAuthToken pour mettre à jour l'utilisateur automatiquement
setAuthToken(token: string): void {
  this.tokenSubject.next(token);
  localStorage.setItem('token', token);
  
  // Décoder le token et mettre à jour l'utilisateur
  const user = this.decodeToken(token);
  if (user) {
    this.setCurrentUser(user);
  }
}

setCurrentUser(user: User): void {
  this.currentUserSubject.next(user);
  localStorage.setItem('currentUser', JSON.stringify(user));
}

// Modifier loginWithGoogle()
loginWithGoogle(): Observable<AuthResponse> {
  return new Observable<AuthResponse>(observer => {
    // Ouvrir une nouvelle fenêtre
    const popup = window.open(
      'http://localhost:8093/oauth2/authorization/google',
      '_blank',
      'width=500,height=600'
    );

    // Écouter les messages
    const messageListener = (event: MessageEvent) => {
      if (event.origin === 'http://localhost:4200' && event.data.token) {
        const response: AuthResponse = {
          token: event.data.token,
          user: event.data.user,
          tokenType: 'Bearer',
          message: 'Google login successful'
        };
        
        this.setAuthToken(response.token);
        this.setCurrentUser(response.user);
        
        observer.next(response);
        observer.complete();
        popup?.close();
        window.removeEventListener('message', messageListener);
      }
    };

    window.addEventListener('message', messageListener, false);
  });
}
}