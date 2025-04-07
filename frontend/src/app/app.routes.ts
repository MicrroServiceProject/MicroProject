import { Routes } from '@angular/router';
import { PostListComponent } from './components/post-list/post-list.component';
import { PostDetailComponent } from './components/post-detail/post-detail.component';
import { PostFormComponent } from './components/post-form/post-form.component';
import { FavoritesComponent } from './components/favorites/favorites.component';
import { AdminComponent } from './components/admin/admin.component';
import { NotificationComponent } from './components/notification/notification.component';
export const routes: Routes = [
  { path: '', component: PostListComponent },
  { path: 'post/:id', component: PostDetailComponent },
  { path: 'new-post', component: PostFormComponent },
  { path: 'edit-post/:id', component: PostFormComponent },
  { path: 'favorites', component: FavoritesComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'notifications', component: NotificationComponent },
  { path: '**', redirectTo: '' }
];