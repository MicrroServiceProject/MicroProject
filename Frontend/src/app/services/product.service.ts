import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8089/api/products';

  private searchQuery = new BehaviorSubject<string>('');
  private favorites: Product[] = [];
  private cart: Product[] = [];

  private favoritesSubject = new BehaviorSubject<Product[]>(this.favorites);
  private cartSubject = new BehaviorSubject<Product[]>(this.cart);

  constructor(private http: HttpClient) { }

  // Get all products
  getProducts(): Observable<Product[]> {
    return this.searchQuery.pipe(
      map(query => query.trim().toLowerCase()),
      // Call backend only if there's a search query
      map(query => {
        if (!query) return null;
        return this.searchProducts(query);
      }),
      // Fallback if no search
      switchMap(result => result || this.http.get<Product[]>(this.apiUrl))
    );
  }

  // Set search query
  setSearchQuery(query: string) {
    this.searchQuery.next(query);
  }

  // Get products by category
  getProductsByCategory(category: 'tools' | 'paintings'): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl).pipe(
      map(products => products.filter(p => p.category === category))
    );
  }

  // Get a product by ID
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  // Create a product
  addProduct(product: Product): Observable<Product> {
    // Remove the id when creating a new product
    const { id, ...productWithoutId } = product;
    return this.http.post<Product>(this.apiUrl, productWithoutId);
  }

  // Update a product
  updateProduct(product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${product.id}`, product);
  }

  // Delete a product
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Search product by name
  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/search?name=${query}`);
  }

  // Favorites
  toggleFavorite(product: Product) {
    const index = this.favorites.findIndex(p => p.id === product.id);
    if (index === -1) {
      this.favorites.push(product);
    } else {
      this.favorites.splice(index, 1);
    }
    this.favoritesSubject.next([...this.favorites]);
  }

  getFavorites(): Observable<Product[]> {
    return this.favoritesSubject.asObservable();
  }

  // Cart
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
