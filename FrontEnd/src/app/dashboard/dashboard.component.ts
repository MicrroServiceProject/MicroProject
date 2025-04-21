import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { UserService } from '../services/user.service';
import { Role } from '../models/user.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  Role = Role;
  sidebarCollapsed = false;
  isDashboardHome = true;
  userStatsByRole: Map<string, number> = new Map();

  totalUsers = 0;
  totalPosts = 0;
  totalEvents = 0;

  // Données formatées pour l'affichage
  rolesForDisplay: any[] = [];
  
  // Couleurs pour les rôles (palette plus moderne)
  roleColors = {
    'ADMIN': '#4361ee',
    'MODERATOR': '#3a86ff',
    'USER': '#4cc9f0'
  };

  constructor(
    private router: Router,
    public userService: UserService
  ) {
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd)
      )
      .subscribe(event => {
        this.isDashboardHome = event.url === '/dashboard';
      });
  }

  // Obtenir la couleur en fonction du rôle
  getRoleColor(roleName: string): string {
    return this.roleColors[roleName.toUpperCase() as keyof typeof this.roleColors] || '#a5a5a5';
  }

  // Obtenir la classe Material en fonction du rôle
  getRoleMaterialColor(roleName: string): string {
    const materialColors = {
      'ADMIN': 'primary',
      'MODERATOR': 'accent',
      'USER': 'warn'
    };
    return materialColors[roleName.toUpperCase() as keyof typeof materialColors] || 'primary';
  }

  ngOnInit(): void {
    this.loadStatistics();
  }
  
  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  // Convertir le nom de couleur en hexadécimal
  getColorHex(colorName: string): string {
    const colors: { [key: string]: string } = {
      'primary': '#3f51b5',
      'accent': '#ff4081',
      'warn': '#f44336'
    };
    return colors[colorName] || '#cccccc';
  }

  // Charger les statistiques depuis l'API backend
  loadStatistics(): void {
    this.userService.getTotalUsers().subscribe({
      next: count => {
        this.totalUsers = count;
        this.userService.getUserStatisticsByRole().subscribe({
          next: stats => {
            this.userStatsByRole = new Map(Object.entries(stats));
            this.prepareRoles();
          }
        });
      }
    });
  }

  // Préparer les données des rôles pour l'affichage
  prepareRoles(): void {
    const rolesArray = Array.from(this.userStatsByRole.entries());
    
    this.rolesForDisplay = rolesArray.map(([roleName, count]) => {
      const percentage = (count / this.totalUsers) * 100;
      
      return {
        name: this.formatRoleName(roleName),
        rawName: roleName,
        count,
        percentage,
        color: this.getRoleMaterialColor(roleName),
        hexColor: this.getRoleColor(roleName)
      };
    });
  
    // Tri par pourcentage décroissant
    this.rolesForDisplay.sort((a, b) => b.percentage - a.percentage);
  }

  // Formater le nom du rôle pour l'affichage
  formatRoleName(roleName: string): string {
    return roleName.charAt(0).toUpperCase() + roleName.slice(1).toLowerCase();
  }

  // Déconnexion
  logout(): void {
    this.userService.logout().subscribe({
      next: () => this.handleLogout(),
      error: (error) => {
        console.error('Logout failed:', error);
        this.handleLogout();
      }
    });
  }

  // Gérer la déconnexion
  private handleLogout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
  }
  getDashOffset(index: number): number {
    return 100 - this.rolesForDisplay
      .slice(0, index)
      .reduce((acc, r) => acc + r.percentage, 0);
  }
  
}