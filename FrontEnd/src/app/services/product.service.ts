import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  getFavorites() {
    throw new Error('Method not implemented.');
  }
  toggleFavorite(product: Product) {
    throw new Error('Method not implemented.');
  }
  private apiUrl = 'http://localhost:8089/api/products';
  private cart: Product[] = [];
  private cartSubject = new BehaviorSubject<Product[]>(this.cart);

  constructor(private http: HttpClient) { }

  // Get all products
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  // Get a product by ID
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  // Create a product
  addProduct(product: Omit<Product, 'id'>): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  // Update a product
  updateProduct(product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${product.id}`, product);
  }

  // Delete a product
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Search products
  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/search?name=${query}`);
  }

  // Cart functionality
  addToCart(product: Product) {
    this.cart.push(product);
    this.cartSubject.next([...this.cart]);
  }

  removeFromCart(productId: number) {
    const index = this.cart.findIndex(p => p.id === productId);
    if (index !== -1) {
      this.cart.splice(index, 1);
      this.cartSubject.next([...this.cart]);
    }
  }

  getCart(): Observable<Product[]> {
    return this.cartSubject.asObservable();
  }

  getCartTotal(): number {
    return this.cart.reduce((total, product) => total + product.price, 0);
  }
}