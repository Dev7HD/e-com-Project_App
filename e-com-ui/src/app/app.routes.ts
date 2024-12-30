import { Routes } from '@angular/router';
import {ProductsComponent} from './components/products/products.component';
import {canActivateAuthRole} from './guards/auth-role.guard';
import {OrdersComponent} from './components/orders/orders.component';
import {CartComponent} from './components/cart/cart.component';

export const routes: Routes = [
  {
    path: 'products',
    component: ProductsComponent,
    canActivate: [canActivateAuthRole],
    data: { role: 'CLIENT' }
  },
  {
    path: 'orders',
    component: OrdersComponent,
    canActivate: [canActivateAuthRole],
    data: { role: 'ADMIN' }
  },
  {
    path: 'cart',
    component: CartComponent,
    canActivate: [canActivateAuthRole],
    data: { role: 'CLIENT' }
  },
  {
    path: 'my-orders',
    component: OrdersComponent,
    canActivate: [canActivateAuthRole],
    data: { role: 'CLIENT' }
  }
];
