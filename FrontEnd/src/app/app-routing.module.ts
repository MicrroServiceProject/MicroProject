import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { AcceuilComponent } from './acceuil/acceuil.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from './auth/auth.guard';
import { EventsComponent } from './events/events.component';

import { ListeUsersComponent } from './liste-users/liste-users.component';
import { EventComponent } from './list-events/list-events.component';
const routes: Routes = [
 
  {path: 'login', component: LoginComponent},
  { path: 'acceuil', component: AcceuilComponent},
 
  { path: 'events', component: EventComponent },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      
      { path: 'users', component: ListeUsersComponent },
      { path: 'events', component: EventsComponent },
    ]
  },
  { path: '', redirectTo: '/acceuil', pathMatch: 'full' },
  { path: '**', redirectTo: '/acceuil' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
