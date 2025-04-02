import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="favorites-container">
      <h1>Mes Favoris</h1>
      
      @if (favorites.length > 0) {
        <div class="favorites-grid">
          @for (product of favorites; track product.id) {
            <div class="favorite-card">
              <div class="image-container">
                <img [src]="product.imageUrl" [alt]="product.name">
                <button 
                  class="remove-btn"
                  (click)="removeFromFavorites(product)">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p class="price">{{ product.price | currency:'EUR' }}</p>
                <div class="actions">
                  <button class="add-to-cart" (click)="addToCart(product)">
                    <i class="fas fa-shopping-cart"></i> Ajouter au panier
                  </button>
                  <a [routerLink]="['/product', product.id]" class="view-details">
                    <i class="fas fa-eye"></i> Voir détails
                  </a>
                </div>
              </div>
            </div>
          }
        </div>
      } @else {
        <div class="empty-state">
          <i class="fas fa-heart-broken"></i>
          <h2>Aucun favori</h2>
          <p>Vous n'avez pas encore ajouté de produits à vos favoris.</p>
          <a routerLink="/" class="browse-products">
            Parcourir les produits
          </a>
        </div>
      }
    </div>
  `,
  styles: [`
    .favorites-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
    }

    h1 {
      font-size: 2rem;
      color: #2c3e50;
      margin-bottom: 30px;
      text-align: center;
    }

    .favorites-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 30px;
    }

    .favorite-card {
      background: white;
      border-radius: 10px;
      overflow: hidden;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      transition: transform 0.3s ease;
    }

    .favorite-card:hover {
      transform: translateY(-5px);
    }

    .image-container {
      position: relative;
      padding-top: 75%;
    }

    .image-container img {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .remove-btn {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(255,255,255,0.9);
      border: none;
      border-radius: 50%;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .remove-btn i {
      color: #e74c3c;
    }

    .remove-btn:hover {
      background: #e74c3c;
    }

    .remove-btn:hover i {
      color: white;
    }

    .product-info {
      padding: 20px;
    }

    .product-info h3 {
      margin: 0;
      font-size: 1.2rem;
      color: #2c3e50;
    }

    .price {
      font-size: 1.25rem;
      color: #3498db;
      font-weight: bold;
      margin: 10px 0;
    }

    .actions {
      display: grid;
      grid-template-columns: 1fr;
      gap: 10px;
      margin-top: 15px;
    }

    .add-to-cart, .view-details {
      padding: 10px;
      border-radius: 5px;
      text-align: center;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }

    .add-to-cart {
      background: #3498db;
      color: white;
      border: none;
    }

    .add-to-cart:hover {
      background: #2980b9;
    }

    .view-details {
      background: white;
      color: #3498db;
      border: 1px solid #3498db;
      text-decoration: none;
    }

    .view-details:hover {
      background: #f8f9fa;
    }

    .empty-state {
      text-align: center;
      padding: 60px 20px;
    }

    .empty-state i {
      font-size: 4rem;
      color: #95a5a6;
      margin-bottom: 20px;
    }

    .empty-state h2 {
      font-size: 1.5rem;
      color: #2c3e50;
      margin-bottom: 10px;
    }

    .empty-state p {
      color: #7f8c8d;
      margin-bottom: 30px;
    }

    .browse-products {
      display: inline-block;
      padding: 12px 24px;
      background: #3498db;
      color: white;
      border-radius: 25px;
      text-decoration: none;
      transition: all 0.3s ease;
    }

    .browse-products:hover {
      background: #2980b9;
      transform: translateY(-2px);
    }

    @media (max-width: 768px) {
      .favorites-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class FavoritesComponent implements OnInit {
  favorites: Product[] = [];

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.productService.getFavorites().subscribe(favorites => {
      this.favorites = favorites;
    });
  }

  removeFromFavorites(product: Product) {
    this.productService.toggleFavorite(product);
  }

  addToCart(product: Product) {
    this.productService.addToCart(product);
  }
}