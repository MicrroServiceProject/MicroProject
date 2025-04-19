import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="admin-container">
      <h1>Administration</h1>
      
      <div class="admin-content">
        <div class="product-form">
          <h2>{{ editingProduct ? 'Modifier le produit' : 'Ajouter un produit' }}</h2>
          <form (submit)="saveProduct()">
            <div class="form-group">
              <label for="name">Nom du produit</label>
              <input 
                type="text" 
                id="name" 
                [(ngModel)]="newProduct.name" 
                name="name" 
                required
              >
            </div>
            
            <div class="form-group">
              <label for="description">Description</label>
              <textarea 
                id="description" 
                [(ngModel)]="newProduct.description" 
                name="description" 
                required
              ></textarea>
            </div>
            
            <div class="form-group">
              <label for="price">Prix (€)</label>
              <input 
                type="number" 
                id="price" 
                [(ngModel)]="newProduct.price" 
                name="price" 
                step="0.01" 
                required
              >
            </div>
            
            <div class="form-group">
              <label for="imageUrl">URL de l'image</label>
              <input 
                type="url" 
                id="imageUrl" 
                [(ngModel)]="newProduct.imageUrl" 
                name="imageUrl" 
                required
              >
            </div>
            
            <div class="form-group">
              <label for="category">Catégorie</label>
              <select 
                id="category" 
                [(ngModel)]="newProduct.category" 
                name="category" 
                required
              >
                <option value="tools">Outils</option>
                <option value="paintings">Tableaux</option>
              </select>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="save-btn">
                {{ editingProduct ? 'Mettre à jour' : 'Ajouter' }}
              </button>
              <button *ngIf="editingProduct" type="button" class="cancel-btn" (click)="cancelEdit()">
                Annuler
              </button>
            </div>
          </form>
        </div>
        
        <div class="products-list">
          <h2>Produits existants</h2>
          <div class="products-grid">
            <div *ngFor="let product of products; trackBy: trackById" class="product-card">
              <img [src]="product.imageUrl" [alt]="product.name">
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p class="price">{{ product.price | currency:'EUR' }}</p>
                <p class="category">{{ product.category === 'tools' ? 'Outils' : 'Tableaux' }}</p>
                <div class="actions">
                  <button class="edit-btn" (click)="editProduct(product)">
                    <i class="fas fa-edit"></i> Modifier
                  </button>
                  <button class="delete-btn" (click)="deleteProduct(product)">
                    <i class="fas fa-trash"></i> Supprimer
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .admin-container {
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

    .admin-content {
      display: grid;
      grid-template-columns: 1fr 2fr;
      gap: 30px;
    }

    .product-form {
      background: white;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    h2 {
      color: #2c3e50;
      margin-bottom: 20px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    label {
      display: block;
      margin-bottom: 5px;
      color: #2c3e50;
    }

    input, textarea, select {
      width: 100%;
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-family: inherit;
    }

    textarea {
      height: 100px;
      resize: vertical;
    }

    .form-actions {
      display: flex;
      gap: 10px;
    }

    .save-btn, .cancel-btn {
      padding: 10px 20px;
      border-radius: 5px;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .save-btn {
      background: #27ae60;
      color: white;
      border: none;
    }

    .save-btn:hover {
      background: #219a52;
    }

    .cancel-btn {
      background: #e74c3c;
      color: white;
      border: none;
    }

    .cancel-btn:hover {
      background: #c0392b;
    }

    .products-list {
      background: white;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
    }

    .product-card {
      border: 1px solid #eee;
      border-radius: 8px;
      overflow: hidden;
    }

    .product-card img {
      width: 100%;
      height: 150px;
      object-fit: cover;
    }

    .product-info {
      padding: 15px;
    }

    .product-info h3 {
      margin: 0;
      color: #2c3e50;
    }

    .price {
      color: #3498db;
      font-weight: bold;
      margin: 5px 0;
    }

    .category {
      color: #7f8c8d;
      font-size: 0.9rem;
      margin-bottom: 10px;
    }

    .actions {
      display: flex;
      gap: 10px;
    }

    .edit-btn, .delete-btn {
      flex: 1;
      padding: 8px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 5px;
    }

    .edit-btn {
      background: #f1c40f;
      color: #2c3e50;
      border: none;
    }

    .edit-btn:hover {
      background: #f39c12;
    }

    .delete-btn {
      background: #e74c3c;
      color: white;
      border: none;
    }

    .delete-btn:hover {
      background: #c0392b;
    }

    @media (max-width: 768px) {
      .admin-content {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class AdminComponent implements OnInit {
  products: Product[] = [];
  newProduct: Product = {
    id: 0,
    name: '',
    description: '',
    price: 0,
    imageUrl: '',
    category: 'tools'
  };
  editingProduct: Product | null = null;

  constructor(private productService: ProductService) { }

  ngOnInit() {
    this.productService.getProducts().subscribe(products => {
      this.products = products;
    });
  }

  saveProduct() {
    if (this.editingProduct) {
      const updatedProduct: Product = {
        ...this.newProduct,
        id: this.editingProduct.id
      };
      this.productService.updateProduct(updatedProduct).subscribe({
        next: () => {
          this.productService.getProducts().subscribe(products => {
            this.products = products;
          });
          this.resetForm();
        },
        error: (error) => {
          console.error('Error updating product:', error);
          alert('Error updating product: ' + error.message);
        }
      });
    } else {
      const newProduct: Product = {
        name: this.newProduct.name,
        description: this.newProduct.description,
        price: this.newProduct.price,
        imageUrl: this.newProduct.imageUrl,
        category: this.newProduct.category
      };

      this.productService.addProduct(newProduct).subscribe({
        next: () => {
          this.productService.getProducts().subscribe(products => {
            this.products = products;
          });
          this.resetForm();
        },
        error: (error) => {
          console.error('Error adding product:', error);
          if (error.status === 400 && error.error === 'Le produit existe déjà !') {
            alert('Un produit avec ce nom existe déjà. Veuillez choisir un autre nom.');
          } else {
            alert('Erreur lors de l\'ajout du produit: ' + (error.error || error.message));
          }
        }
      });
    }
  }

  editProduct(product: Product) {
    this.editingProduct = product;
    this.newProduct = { ...product };
  }

  deleteProduct(product: Product) {
    if (!product.id) {
      console.error('Cannot delete product without ID');
      return;
    }
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.deleteProduct(product.id).subscribe(() => {
        this.productService.getProducts().subscribe(products => {
          this.products = products;
        });
      });
    }
  }

  cancelEdit() {
    this.resetForm();
  }

  private resetForm() {
    this.editingProduct = null;
    this.newProduct = {
      id: 0,
      name: '',
      description: '',
      price: 0,
      imageUrl: '',
      category: 'tools'
    };
  }

  trackById(index: number, product: Product): number {
    return product.id ?? index;  // Use index as fallback if id is undefined
  }
}
