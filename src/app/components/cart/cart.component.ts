import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="cart-container">
      <h1>Mon Panier</h1>
      
      @if (cartItems.length > 0) {
        <div class="cart-content">
          <div class="cart-items">
            @for (item of cartItems; track item.id) {
              <div class="cart-item">
                <img [src]="item.imageUrl" [alt]="item.name">
                <div class="item-details">
                  <h3>{{ item.name }}</h3>
                  <p class="price">{{ item.price | currency:'EUR' }}</p>
                </div>
                <button class="remove-btn" (click)="removeFromCart(item)">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            }
          </div>
          
          <div class="cart-summary">
            <h2>Résumé de la commande</h2>
            <div class="summary-details">
              <div class="summary-row">
                <span>Sous-total</span>
                <span>{{ getSubtotal() | currency:'EUR' }}</span>
              </div>
              <div class="summary-row">
                <span>TVA (20%)</span>
                <span>{{ getTax() | currency:'EUR' }}</span>
              </div>
              <div class="summary-row total">
                <span>Total</span>
                <span>{{ getTotal() | currency:'EUR' }}</span>
              </div>
            </div>
            <button class="checkout-btn" (click)="checkout()">
              <i class="fas fa-lock"></i> Procéder au paiement
            </button>
          </div>
        </div>
      } @else {
        <div class="empty-state">
          <i class="fas fa-shopping-cart"></i>
          <h2>Votre panier est vide</h2>
          <p>Ajoutez des produits à votre panier pour commencer vos achats.</p>
          <a routerLink="/" class="continue-shopping">
            Continuer mes achats
          </a>
        </div>
      }
    </div>
  `,
  styles: [`
    .cart-container {
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

    .cart-content {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: 30px;
    }

    .cart-items {
      background: white;
      border-radius: 10px;
      padding: 20px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    .cart-item {
      display: flex;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #eee;
    }

    .cart-item:last-child {
      border-bottom: none;
    }

    .cart-item img {
      width: 100px;
      height: 100px;
      object-fit: cover;
      border-radius: 5px;
    }

    .item-details {
      flex: 1;
      padding: 0 20px;
    }

    .item-details h3 {
      margin: 0;
      color: #2c3e50;
    }

    .price {
      color: #3498db;
      font-weight: bold;
      margin-top: 5px;
    }

    .remove-btn {
      background: none;
      border: none;
      color: #e74c3c;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .remove-btn:hover {
      color: #c0392b;
      transform: scale(1.1);
    }

    .cart-summary {
      background: white;
      border-radius: 10px;
      padding: 20px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      height: fit-content;
    }

    .cart-summary h2 {
      color: #2c3e50;
      margin-bottom: 20px;
    }

    .summary-details {
      margin-bottom: 20px;
    }

    .summary-row {
      display: flex;
      justify-content: space-between;
      padding: 10px 0;
      color: #7f8c8d;
    }

    .summary-row.total {
      border-top: 2px solid #eee;
      margin-top: 10px;
      padding-top: 20px;
      font-weight: bold;
      color: #2c3e50;
      font-size: 1.2rem;
    }

    .checkout-btn {
      width: 100%;
      padding: 15px;
      background: #27ae60;
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 1.1rem;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .checkout-btn:hover {
      background: #219a52;
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

    .continue-shopping {
      display: inline-block;
      padding: 12px 24px;
      background: #3498db;
      color: white;
      border-radius: 25px;
      text-decoration: none;
      transition: all 0.3s ease;
    }

    .continue-shopping:hover {
      background: #2980b9;
      transform: translateY(-2px);
    }

    @media (max-width: 768px) {
      .cart-content {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class CartComponent implements OnInit {
  cartItems: Product[] = [];

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.productService.getCart().subscribe(items => {
      this.cartItems = items;
    });
  }

  removeFromCart(product: Product) {
    this.productService.removeFromCart(product.id);
  }

  getSubtotal(): number {
    return this.cartItems.reduce((total, item) => total + item.price, 0);
  }

  getTax(): number {
    return this.getSubtotal() * 0.2;
  }

  getTotal(): number {
    return this.getSubtotal() + this.getTax();
  }

  async checkout() {
    // Here you would integrate with a payment processor like Stripe
    alert('Redirection vers le système de paiement...');
  }
}