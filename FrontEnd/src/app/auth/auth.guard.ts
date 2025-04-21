import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { Role } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(): boolean {
    const currentUser = this.userService.getCurrentUser();
    if (currentUser && currentUser.role === Role.ADMIN) {
      return true; // Allow access for admin users
    }

    // Redirect non-admin users to acceuil
    this.router.navigate(['/acceuil']);
    return false;
  }
}