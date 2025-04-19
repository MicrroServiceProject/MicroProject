import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container">
      <div class="filters">
        <button 
          [class.active]="currentCategory === 'all'"
          (click)="filterByCategory('all')">
          Tous les produits
        </button>
        <button 
          [class.active]="currentCategory === 'tools'"
          (click)="filterByCategory('tools')">
          Outils
        </button>
        <button 
          [class.active]="currentCategory === 'paintings'"
          (click)="filterByCategory('paintings')">
          Tableaux
        </button>
      </div>

      <div class="products-grid">
        @for (product of filteredProducts; track product.id) {
          <div class="product-card">
            <div class="image-container">
              <img [src]="product.imageUrl" [alt]="product.name">
              <button 
                class="favorite-btn"
               
                [class.active]="isFavorite(product)">
                ❤
              </button>
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">{{ product.price | currency:'EUR' }}</p>
              <div class="actions">
                <button class="cart-btn" (click)="addToCart(product)">
                  Ajouter au panier
                </button>
                <a [routerLink]="['/product', product.id]" class="details-btn">
                  Voir détails
                </a>
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .filters {
      margin-bottom: 30px;
      display: flex;
      gap: 15px;
      justify-content: center;
    }

    .filters button {
      padding: 10px 20px;
      border: 2px solid #007bff;
      background: white;
      color: #007bff;
      border-radius: 25px;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .filters button.active {
      background: #007bff;
      color: white;
    }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 30px;
    }

    .product-card {
      border: 1px solid #eee;
      border-radius: 8px;
      overflow: hidden;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .product-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
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

    .favorite-btn {
      position: absolute;
      top: 10px;
      right: 10px;
      background: white;
      border: none;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 20px;
      color: #ccc;
      transition: all 0.3s ease;
    }

    .favorite-btn.active {
      color: #ff4081;
    }

    .product-info {
      padding: 15px;
    }

    .product-info h3 {
      margin: 0;
      font-size: 1.2rem;
      color: #333;
    }

    .price {
      font-size: 1.25rem;
      color: #007bff;
      font-weight: bold;
      margin: 10px 0;
    }

    .actions {
      display: flex;
      gap: 10px;
      margin-top: 15px;
    }

    .cart-btn, .details-btn {
      flex: 1;
      padding: 8px;
      border-radius: 4px;
      text-align: center;
      transition: all 0.3s ease;
    }

    .cart-btn {
      background: #007bff;
      color: white;
      border: none;
    }

    .cart-btn:hover {
      background: #0056b3;
    }

    .details-btn {
      background: white;
      color: #007bff;
      border: 1px solid #007bff;
      text-decoration: none;
    }

    .details-btn:hover {
      background: #f8f9fa;
    }
  `]
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  favorites: Product[] = [];
  currentCategory: 'all' | 'tools' | 'paintings' = 'all';

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.productService.getProducts().subscribe(products => {
      this.products = products;
      this.filterByCategory(this.currentCategory);
    });
    
  }
  filterByCategory(category: 'all' | 'tools' | 'paintings') {
    this.currentCategory = category;
    if (category === 'all') {
      this.filteredProducts = this.products;
    } else {
      this.filteredProducts = this.products.filter(p => p.category === category);
    }
  }

  

  isFavorite(product: Product): boolean {
    return this.favorites.some(p => p.id === product.id);
  }

  addToCart(product: Product) {
    this.productService.addToCart(product);
  }
}