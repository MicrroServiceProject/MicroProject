import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { User, Role } from '../../models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  registerForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    // Initialize the login form
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Initialize the register form
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern("^[0-9]*$")]]
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    // Redirect if already logged in
    if (this.userService.isLoggedIn()) {
      this.router.navigate(['/acceuil']);
    }
  }

  // Custom validator to check if passwords match
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ 'passwordMismatch': true });
    }
  }

  // Login method
  onLogin() {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    const { email, password } = this.loginForm.value;

    this.userService.login(email, password).subscribe({
      next: (response) => {
        this.successMessage = 'Login successful!';
        this.errorMessage = '';

        // Redirect based on user role
        const userRole = response.user.role;
        if (userRole === Role.ADMIN) {
          this.router.navigate(['/dashboard']); // Redirect to dashboard for admin
        } else {
          this.router.navigate(['/acceuil']); // Redirect to acceuil for other roles
        }
      },
      error: (error) => {
        this.errorMessage = error.message || 'Login failed. Please check your credentials.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  // Register method
  onRegister() {
    if (this.registerForm.invalid) {
      return;
    }
  
    this.loading = true;
    const formData = this.registerForm.value;
    
    // Create user object without userId - let the server assign it
    const newUser: Partial<User> = {
      username: formData.username,
      email: formData.email,
      password: formData.password,
      firstName: formData.firstName,
      lastName: formData.lastName,
      phoneNumber: formData.phoneNumber,
      active: true,
      role: Role.USER
    };
  
    // Use the 'as User' cast or update your service method signature
    this.userService.register(newUser as User).subscribe({
      next: (response) => {
        this.successMessage = 'Registration successful!';
        this.errorMessage = '';
        
        // Navigate to the acceuil component after successful registration
        setTimeout(() => {
          this.router.navigate(['/acceuil']);
        }, 1000);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Registration failed. Please try again.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  // Check for input validation errors
  hasError(formGroup: FormGroup, controlName: string, errorType: string): boolean {
    const control = formGroup.get(controlName);
    return control !== null && control.hasError(errorType) && control.touched;
  }
  // Connexion via Google OAuth2
// Mettez à jour la méthode loginWithGoogle
loginWithGoogle(): void {
  this.loading = true;
  
  this.userService.loginWithGoogle().subscribe({
    next: (response) => {
      this.successMessage = 'Google login successful!';
      this.router.navigate(['/acceuil']);
    },
    error: (error) => {
      this.errorMessage = 'Google login failed. Please try again.';
      this.loading = false;
    }
  });
}
}