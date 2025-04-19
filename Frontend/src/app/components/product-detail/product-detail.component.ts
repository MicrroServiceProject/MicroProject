import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (product) {
      <div class="product-detail">
        <div class="image-section">
          <img [src]="product.imageUrl" [alt]="product.name">
          <button 
            class="favorite-btn"
            (click)="toggleFavorite()"
            [class.active]="isFavorite">
            ❤
          </button>
        </div>
        <div class="info-section">
          <h1>{{ product.name }}</h1>
          <p class="category">{{ product.category === 'tools' ? 'Outils' : 'Tableaux' }}</p>
          <p class="price">{{ product.price | currency:'EUR' }}</p>
          <div class="description">
            <h2>Description</h2>
            <p>{{ product.description }}</p>
          </div>
          <div class="actions">
            <button class="add-to-cart" (click)="addToCart()">
              Ajouter au panier
            </button>
            <button class="buy-now">
              Acheter maintenant
            </button>
          </div>
          @if (isAdmin) {
            <div class="admin-actions">
              <button class="edit" (click)="editProduct()">Modifier</button>
              <button class="delete" (click)="deleteProduct()">Supprimer</button>
            </div>
          }
        </div>
      </div>
    } @else {
      <div class="error">
        <h2>Produit non trouvé</h2>
        <button (click)="goBack()">Retour aux produits</button>
      </div>
    }
  `,
  styles: [`
    .product-detail {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 40px;
      max-width: 1200px;
      margin: 40px auto;
      padding: 0 20px;
    }

    .image-section {
      position: relative;
    }

    .image-section img {
      width: 100%;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }

    .favorite-btn {
      position: absolute;
      top: 20px;
      right: 20px;
      background: white;
      border: none;
      border-radius: 50%;
      width: 50px;
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 24px;
      color: #ccc;
      transition: all 0.3s ease;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .favorite-btn.active {
      color: #ff4081;
    }

    .info-section {
      padding: 20px;
    }

    h1 {
      font-size: 2.5rem;
      margin: 0 0 10px 0;
      color: #333;
    }

    .category {
      color: #666;
      font-size: 1.1rem;
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 20px;
    }

    .price {
      font-size: 2rem;
      color: #007bff;
      font-weight: bold;
      margin-bottom: 30px;
    }

    .description {
      margin-bottom: 40px;
    }

    .description h2 {
      font-size: 1.5rem;
      margin-bottom: 15px;
      color: #333;
    }

    .description p {
      line-height: 1.6;
      color: #666;
    }

    .actions {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 30px;
    }

    .actions button {
      padding: 15px;
      border-radius: 8px;
      font-size: 1.1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .add-to-cart {
      background: white;
      color: #007bff;
      border: 2px solid #007bff;
    }

    .add-to-cart:hover {
      background: #f8f9fa;
    }

    .buy-now {
      background: #007bff;
      color: white;
      border: none;
    }

    .buy-now:hover {
      background: #0056b3;
    }

    .admin-actions {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #eee;
    }

    .admin-actions button {
      padding: 10px;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
    }

    .edit {
      background: #ffd700;
      border: none;
      color: #333;
    }

    .delete {
      background: #dc3545;
      border: none;
      color: white;
    }

    .error {
      text-align: center;
      padding: 40px;
    }

    .error h2 {
      margin-bottom: 20px;
      color: #dc3545;
    }

    @media (max-width: 768px) {
      .product-detail {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class ProductDetailComponent implements OnInit {
  product: Product | undefined;
  isFavorite = false;
  isAdmin = true; // In a real app, this would come from an auth service

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.productService.getProducts().subscribe(products => {
        this.product = products.find(p => p.id === +params['id']);
        
      });
    });
  }

  toggleFavorite() {
    if (this.product) {
      this.productService.toggleFavorite(this.product);
      this.isFavorite = !this.isFavorite;
    }
  }

  addToCart() {
    if (this.product) {
      this.productService.addToCart(this.product);
    }
  }

  editProduct() {
    // This would navigate to an edit form in a real application
    console.log('Edit product:', this.product);
  }

  deleteProduct() {
    if (!this.product?.id) {
      console.error('Cannot delete product without ID');
      return;
    }
    this.productService.deleteProduct(this.product.id);
  }

  goBack() {
    this.router.navigate(['/']);
  }
}