import { Routes } from '@angular/router';
import {ProductsComponent} from './components/products/products.component';
import {canActivateAuthRole} from './guards/auth-role.guard';

export const routes: Routes = [
  {
    path: 'products',
    component: ProductsComponent,
    canActivate: [canActivateAuthRole],
    data: { role: 'CLIENT' }
  }
];
