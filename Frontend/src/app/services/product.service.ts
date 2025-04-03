import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private products: Product[] = [
    {
      id: 1,
      name: 'Pinceau Professionnel',
      description: 'Pinceau de haute qualité pour artistes professionnels. Parfait pour l\'acrylique et l\'huile.',
      price: 24.99,
      imageUrl: 'https://images.unsplash.com/photo-1515467837915-15c4777cd75a?w=500',
      category: 'tools'
    },
    {
      id: 2,
      name: 'Set de Peinture Acrylique',
      description: 'Ensemble complet de 24 couleurs acryliques pour artistes.',
      price: 49.99,
      imageUrl: 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=500',
      category: 'tools'
    },
    {
      id: 3,
      name: 'Coucher de Soleil',
      description: 'Magnifique peinture à l\'huile représentant un coucher de soleil sur l\'océan.',
      price: 299.99,
      imageUrl: 'https://images.unsplash.com/photo-1544867885-2333f61544ad?w=500',
      category: 'paintings'
    },
    {
      id: 4,
      name: 'Abstrait Moderne',
      description: 'Œuvre d\'art abstraite contemporaine avec des couleurs vives.',
      price: 449.99,
      imageUrl: 'https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=500',
      category: 'paintings'
    }
  ];

  private favorites: Product[] = [];
  private cart: Product[] = [];
  private searchQuery = new BehaviorSubject<string>('');

  private productsSubject = new BehaviorSubject<Product[]>(this.products);
  private favoritesSubject = new BehaviorSubject<Product[]>(this.favorites);
  private cartSubject = new BehaviorSubject<Product[]>(this.cart);

  getProducts(): Observable<Product[]> {
    return this.searchQuery.pipe(
      map(query => {
        if (!query) return this.products;
        const lowercaseQuery = query.toLowerCase();
        return this.products.filter(product =>
          product.name.toLowerCase().includes(lowercaseQuery) ||
          product.description.toLowerCase().includes(lowercaseQuery)
        );
      })
    );
  }

  setSearchQuery(query: string) {
    this.searchQuery.next(query);
  }

  getFavorites(): Observable<Product[]> {
    return this.favoritesSubject.asObservable();
  }

  getCart(): Observable<Product[]> {
    return this.cartSubject.asObservable();
  }

  getProductsByCategory(category: 'tools' | 'paintings'): Observable<Product[]> {
    return this.getProducts().pipe(
      map(products => products.filter(p => p.category === category))
    );
  }

  addProduct(product: Product) {
    const newId = Math.max(...this.products.map(p => p.id)) + 1;
    const newProduct = { ...product, id: newId };
    this.products.push(newProduct);
    this.productsSubject.next([...this.products]);
  }

  updateProduct(product: Product) {
    const index = this.products.findIndex(p => p.id === product.id);
    if (index !== -1) {
      this.products[index] = product;
      this.productsSubject.next([...this.products]);
    }
  }

  deleteProduct(id: number) {
    this.products = this.products.filter(p => p.id !== id);
    this.productsSubject.next([...this.products]);
    this.favorites = this.favorites.filter(p => p.id !== id);
    this.favoritesSubject.next([...this.favorites]);
    this.cart = this.cart.filter(p => p.id !== id);
    this.cartSubject.next([...this.cart]);
  }

  toggleFavorite(product: Product) {
    const index = this.favorites.findIndex(p => p.id === product.id);
    if (index === -1) {
      this.favorites.push(product);
    } else {
      this.favorites.splice(index, 1);
    }
    this.favoritesSubject.next([...this.favorites]);
  }

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

  getCartTotal(): number {
    return this.cart.reduce((total, product) => total + product.price, 0);
  }
}