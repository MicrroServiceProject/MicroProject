import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { User } from '../models/user.model';

@Component({
  selector: 'app-oauth2-redirect',
  templateUrl: './oauth2-redirect.component.html',
  styleUrls: ['./oauth2-redirect.component.css']
})
export class Oauth2RedirectComponent implements OnInit {
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.handleOAuth2Login(token);
      } else {
        this.errorMessage = 'Error: OAuth2 token is missing.';
        this.loading = false;
      }
    });
  }

  private handleOAuth2Login(token: string): void {
    this.userService.setAuthToken(token);
    
    // Décoder le token pour obtenir les infos utilisateur
    const user = this.userService.decodeToken(token);
    
    if (user) {
      try {
        this.userService.setCurrentUser(user);
        window.opener.postMessage({ 
          token: token,
          user: user 
        }, window.location.origin);
        window.close();
      } catch (e) {
        this.errorMessage = 'Failed to communicate with parent window';
        this.loading = false;
      }
    } else {
      this.errorMessage = 'Failed to decode user data';
      this.loading = false;
    }
  }
}