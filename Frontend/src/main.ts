import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductListComponent } from './app/components/product-list/product-list.component';
import { ProductDetailComponent } from './app/components/product-detail/product-detail.component';
import { FavoritesComponent } from './app/components/favorites/favorites.component';
import { CartComponent } from './app/components/cart/cart.component';
import { AdminComponent } from './app/components/admin/admin.component';
import { ProductService } from './app/services/product.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  template: `
    <header class="main-header">
      <div class="header-content">
        <div class="logo-section">
          <a [routerLink]="['/']" class="logo">
            <i class="fas fa-palette"></i>
            <span>Art Shop</span>
          </a>
        </div>
        
        <div class="search-bar">
          <input 
            type="text" 
            [(ngModel)]="searchQuery" 
            (keyup)="onSearch()"
            placeholder="Rechercher des produits..."
          >
          <i class="fas fa-search"></i>
        </div>

        <nav class="main-nav">
          <a [routerLink]="['/']" 
             [routerLinkActive]="['active']" 
             [routerLinkActiveOptions]="{exact: true}">
            <i class="fas fa-home"></i>
            <span>Accueil</span>
          </a>
          <a [routerLink]="['/favorites']" 
             [routerLinkActive]="['active']" 
             class="favorites-link">
            <i class="fas fa-heart"></i>
            <span>Favoris</span>
            <span *ngIf="favoritesCount > 0" class="badge">{{ favoritesCount }}</span>
          </a>
          <a [routerLink]="['/cart']" 
             [routerLinkActive]="['active']" 
             class="cart-link">
            <i class="fas fa-shopping-cart"></i>
            <span>Panier</span>
            <span *ngIf="cartCount > 0" class="badge">{{ cartCount }}</span>
          </a>
          <a *ngIf="isAdmin" 
             [routerLink]="['/admin']" 
             [routerLinkActive]="['active']">
            <i class="fas fa-cog"></i>
            <span>Admin</span>
          </a>
        </nav>
      </div>
    </header>

    <main>
      <router-outlet></router-outlet>
    </main>

    <footer class="main-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>À propos</h3>
          <p>Art Shop est votre destination pour l'art et les fournitures artistiques de qualité.</p>
        </div>
        
        <div class="footer-section">
          <h3>Liens rapides</h3>
          <ul>
            <li><a routerLink="/about">À propos</a></li>
            <li><a routerLink="/contact">Contact</a></li>
            <li><a routerLink="/terms">Conditions d'utilisation</a></li>
            <li><a routerLink="/privacy">Politique de confidentialité</a></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>Contact</h3>
          <p>
            <i class="fas fa-envelope"></i> <span>contact&#64;artshop.com</span><br>
            <i class="fas fa-phone"></i> +33 1 23 45 67 89<br>
            <i class="fas fa-map-marker-alt"></i> Paris, France
          </p>
        </div>
        
        <div class="footer-section">
          <h3>Suivez-nous</h3>
          <div class="social-links">
            <a href="#"><i class="fab fa-facebook"></i></a>
            <a href="#"><i class="fab fa-instagram"></i></a>
            <a href="#"><i class="fab fa-twitter"></i></a>
            <a href="#"><i class="fab fa-pinterest"></i></a>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2024 Art Shop. Tous droits réservés.</p>
      </div>
    </footer>
  `,
  styles: [`
    /* Styles généraux */
    :host {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    main {
      flex: 1;
      padding: 20px;
      background-color: #f8f9fa;
    }

    /* Styles du Header */
    .main-header {
      background-color: #ffffff;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      padding: 15px 0;
      position: sticky;
      top: 0;
      z-index: 1000;
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 20px;
    }

    .logo-section {
      flex: 0 0 auto;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 10px;
      text-decoration: none;
      color: #007bff;
      font-size: 1.5rem;
      font-weight: bold;
    }

    .logo i {
      font-size: 1.8rem;
    }

    /* Barre de recherche */
    .search-bar {
      flex: 1;
      max-width: 500px;
      position: relative;
    }

    .search-bar input {
      width: 100%;
      padding: 10px 40px 10px 15px;
      border: 2px solid #e9ecef;
      border-radius: 25px;
      font-size: 1rem;
      transition: border-color 0.3s ease;
    }

    .search-bar input:focus {
      outline: none;
      border-color: #007bff;
    }

    .search-bar i {
      position: absolute;
      right: 15px;
      top: 50%;
      transform: translateY(-50%);
      color: #6c757d;
    }

    /* Navigation */
    .main-nav {
      display: flex;
      gap: 20px;
      align-items: center;
    }

    .main-nav a {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-decoration: none;
      color: #495057;
      font-size: 0.9rem;
      position: relative;
      padding: 5px 10px;
      transition: color 0.3s ease;
    }

    .main-nav a i {
      font-size: 1.2rem;
      margin-bottom: 4px;
    }

    .main-nav a:hover {
      color: #007bff;
    }

    .main-nav a.active {
      color: #007bff;
    }

    .badge {
      position: absolute;
      top: -8px;
      right: -8px;
      background-color: #dc3545;
      color: white;
      font-size: 0.75rem;
      padding: 2px 6px;
      border-radius: 10px;
      min-width: 18px;
      text-align: center;
    }

    /* Styles du Footer */
    .main-footer {
      background-color: #2c3e50;
      color: #ffffff;
      padding: 40px 0 0;
      margin-top: auto;
    }

    .footer-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 40px;
    }

    .footer-section {
      margin-bottom: 30px;
    }

    .footer-section h3 {
      color: #ffffff;
      font-size: 1.2rem;
      margin-bottom: 20px;
      position: relative;
    }

    .footer-section h3::after {
      content: '';
      position: absolute;
      left: 0;
      bottom: -8px;
      width: 40px;
      height: 2px;
      background-color: #007bff;
    }

    .footer-section p {
      color: #bdc3c7;
      line-height: 1.6;
    }

    .footer-section ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .footer-section ul li {
      margin-bottom: 10px;
    }

    .footer-section ul a {
      color: #bdc3c7;
      text-decoration: none;
      transition: color 0.3s ease;
    }

    .footer-section ul a:hover {
      color: #ffffff;
    }

    .footer-section i {
      width: 20px;
      margin-right: 10px;
    }

    .social-links {
      display: flex;
      gap: 15px;
    }

    .social-links a {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      background-color: rgba(255,255,255,0.1);
      border-radius: 50%;
      color: #ffffff;
      text-decoration: none;
      transition: all 0.3s ease;
    }

    .social-links a:hover {
      background-color: #007bff;
      transform: translateY(-3px);
    }

    .footer-bottom {
      background-color: #243342;
      padding: 20px 0;
      margin-top: 40px;
      text-align: center;
    }

    .footer-bottom p {
      margin: 0;
      color: #bdc3c7;
      font-size: 0.9rem;
    }

    /* Media Queries pour la responsivité */
    @media (max-width: 768px) {
      .header-content {
        flex-direction: column;
        gap: 15px;
      }

      .search-bar {
        max-width: 100%;
      }

      .main-nav {
        width: 100%;
        justify-content: space-around;
      }

      .footer-content {
        grid-template-columns: 1fr;
      }

      .footer-section {
        text-align: center;
      }

      .footer-section h3::after {
        left: 50%;
        transform: translateX(-50%);
      }

      .social-links {
        justify-content: center;
      }
    }

    @media (max-width: 480px) {
      .main-nav span {
        display: none;
      }

      .main-nav a i {
        font-size: 1.5rem;
        margin-bottom: 0;
      }

      .badge {
        top: -5px;
        right: -5px;
      }
    }
  `]
})
export class App {
  searchQuery = '';
  favoritesCount = 0;
  cartCount = 0;
  isAdmin = true;

  constructor(private productService: ProductService) {
    this.productService.getFavorites().subscribe(favorites => {
      this.favoritesCount = favorites.length;
    });

    this.productService.getCart().subscribe(cart => {
      this.cartCount = cart.length;
    });
  }

  onSearch() {
    this.productService.setSearchQuery(this.searchQuery);
  }
}

const routes = [
  { path: '', component: ProductListComponent },
  { path: 'product/:id', component: ProductDetailComponent },
  { path: 'favorites', component: FavoritesComponent },
  { path: 'cart', component: CartComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: '' }
];

bootstrapApplication(App, {
  providers: [
    provideRouter(routes)
  ]
}).catch(err => console.error(err));
