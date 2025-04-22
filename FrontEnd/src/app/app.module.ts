import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Combined imports
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TokenInterceptor } from './interceptors/token.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';  // Ajoute cette ligne
import { MatProgressBarModule } from '@angular/material/progress-bar';

// Material Modules
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule } from '@angular/material/dialog';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { EventsComponent } from './events/events.component';
import { AcceuilComponent } from './acceuil/acceuil.component';
import { LoginComponent } from './auth/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Oauth2RedirectComponent } from './oauth2-redirect/oauth2-redirect.component';
import { ListeUsersComponent } from './liste-users/liste-users.component';
import { EventComponent } from './list-events/list-events.component';
import { RouterModule } from '@angular/router';
import { MaterialComponent } from './material/material.component';
import { CoursComponent } from './cours/cours.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { ProductListComponent } from './product-list/product-list.component';
import { CartComponent } from './cart/cart.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    
    FooterComponent,
    EventsComponent,
    AcceuilComponent,
    LoginComponent,
    DashboardComponent,
    
    Oauth2RedirectComponent,
    ListeUsersComponent,
    EventComponent,
    MaterialComponent,
    CoursComponent,
   // Add this declaration
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule,
    ReactiveFormsModule, // For formGroup
    FormsModule, // For template-driven forms
    AppRoutingModule,
    BrowserAnimationsModule,
    // Material Modules
    MatProgressSpinnerModule,  // Ajoute cette ligne ici
    MatProgressBarModule,
    MatIconModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    MatInputModule,
    MatTableModule,
    MatDialogModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }