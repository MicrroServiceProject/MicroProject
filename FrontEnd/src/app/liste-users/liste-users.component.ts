import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../models/user.model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-liste-users',
  templateUrl: './liste-users.component.html',
  styleUrls: ['./liste-users.component.css']
})
export class ListeUsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  editingUserId: number | null = null;
  editedUser: User | null = null;
  isLoading = true;
  errorMessage = '';
  saveLoading = false;
  
  // Formulaire de recherche avancée
  searchForm: FormGroup;
  
  // Options de rôles pour le filtre
  roleOptions = [
    { value: '', label: 'All Roles' },
    { value: 'ADMIN', label: 'Admin' },
    { value: 'USER', label: 'User' },
    { value: 'MODERATOR', label: 'Moderator' }
  ];
  
  // Options de statut pour le filtre
  statusOptions = [
    { value: '', label: 'All Status' },
    { value: 'true', label: 'Active' },
    { value: 'false', label: 'Inactive' }
  ];

  constructor(
    private userService: UserService,
    private fb: FormBuilder
  ) {
    // Initialisation du formulaire de recherche
    this.searchForm = this.fb.group({
      searchTerm: [''],
      role: [''],
      status: [''],
      sortBy: ['userId'],
      sortDirection: ['asc']
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    
    // Observer les changements dans le formulaire de recherche
    this.searchForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged((prev, curr) => 
          JSON.stringify(prev) === JSON.stringify(curr)
        )
      )
      .subscribe(() => {
        this.applyFilters();
      });
  }

  loadUsers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = [...users]; // Copie initiale
        this.applyFilters(); // Appliquer les filtres initiaux
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load users. Please try again later.';
        this.isLoading = false;
        console.error('Error loading users:', err);
      }
    });
  }

  // Appliquer tous les filtres
  applyFilters(): void {
    const { searchTerm, role, status, sortBy, sortDirection } = this.searchForm.value;
    
    // Filtrer les utilisateurs
    this.filteredUsers = this.users.filter(user => {
      // Filtre par terme de recherche (recherche dans plusieurs champs)
      const searchMatch = !searchTerm || 
        user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (user.firstName && user.firstName.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (user.lastName && user.lastName.toLowerCase().includes(searchTerm.toLowerCase()));
      
      // Filtre par rôle
      const roleMatch = !role || user.role === role;
      
      // Filtre par statut
      const statusMatch = status === '' || 
        (status === 'true' && user.active) || 
        (status === 'false' && !user.active);
      
      return searchMatch && roleMatch && statusMatch;
    });
    
    // Trier les résultats
    this.sortUsers(sortBy, sortDirection);
  }
  
  // Trier les utilisateurs
  sortUsers(sortBy: string, sortDirection: string): void {
    this.filteredUsers.sort((a, b) => {
      let comparison = 0;
      
      // Déterminer la valeur à comparer en fonction du champ de tri
      switch (sortBy) {
        case 'username':
          comparison = a.username.localeCompare(b.username);
          break;
        case 'email':
          comparison = a.email.localeCompare(b.email);
          break;
        case 'firstName':
          comparison = (a.firstName || '').localeCompare(b.firstName || '');
          break;
        case 'lastName':
          comparison = (a.lastName || '').localeCompare(b.lastName || '');
          break;
        case 'role':
          comparison = a.role.localeCompare(b.role);
          break;
        case 'status':
          comparison = Number(a.active) - Number(b.active);
          break;
        default: // userId par défaut
          comparison = a.userId - b.userId;
      }
      
      // Appliquer la direction du tri
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }
  
  // Réinitialiser tous les filtres
  resetFilters(): void {
    this.searchForm.reset({
      searchTerm: '',
      role: '',
      status: '',
      sortBy: 'userId',
      sortDirection: 'asc'
    });
  }

  startEdit(user: User): void {
    this.editingUserId = user.userId;
    this.editedUser = { ...user }; // Shallow copy (OK pour les données simples)
  }

  cancelEdit(): void {
    this.editingUserId = null;
    this.editedUser = null;
  }

  saveUser(): void {
    if (this.editedUser) {
      this.saveLoading = true;
      
      // Créer un payload sans le mot de passe
      const payload = {
        username: this.editedUser.username,
        email: this.editedUser.email,
        firstName: this.editedUser.firstName,
        lastName: this.editedUser.lastName,
        role: this.editedUser.role,
        active: this.editedUser.active
      };
  
      this.userService.updateUser(this.editedUser.userId, payload).subscribe({
        next: (updatedUser) => {
          // Mettre à jour l'utilisateur dans le tableau
          const index = this.users.findIndex(u => u.userId === this.editedUser!.userId);
          if (index !== -1) {
            this.users[index] = { ...this.users[index], ...payload };
          }
          
          // Réappliquer les filtres
          this.applyFilters();
          
          // Réinitialiser l'état d'édition
          this.editingUserId = null;
          this.editedUser = null;
          this.saveLoading = false;
        },
        error: (err) => {
          console.error('Error updating user:', err);
          this.errorMessage = 'Failed to update user.';
          this.saveLoading = false;
        }
      });
    }
  }

  deleteUser(userId: number): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe({
        next: () => {
          this.users = this.users.filter(user => user.userId !== userId);
          this.applyFilters(); // Réappliquer les filtres après suppression
        },
        error: (err) => {
          console.error('Error deleting user:', err);
          this.errorMessage = 'Failed to delete user.';
        }
      });
    }
  }
}