import {Component, inject} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {OrderService} from '../../services/order.service';
import {DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {Order} from '../../models/order.model';
import {ToastrService} from 'ngx-toastr';
import {NgbAlert} from '@ng-bootstrap/ng-bootstrap';
import {Product} from '../../models/product.model';

@Component({
  selector: 'app-cart',
  imports: [
    RouterLink,
    NgForOf,
    FormsModule,
    DecimalPipe,
    NgIf,
    NgbAlert
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent {

  orderService: OrderService = inject(OrderService);
  private _http: HttpClient = inject(HttpClient);
  private _toastr: ToastrService = inject(ToastrService);
  private _router = inject(Router)

  isSaving: boolean = false;

  checkoutCart(){
    this.isSaving = true;
    let productList: Product[] = [];

    this.orderService.shoppingCart.cardProductList.forEach(i => {
      let product: Product;
      product = {...i, quantity: i.quantityInCart}
      productList.push(product)
    })

    this._http.post<Order>(`${environment.orderURL}/api/orders/new`, productList).subscribe({
      next: data => {
        this.isSaving = false;
        console.log(data);
        this._toastr.success('Order successfully created', 'Success');
        this.orderService.resetShoppingCart();
        this._router.navigateByUrl('/products')
      }, error: error => {
        this.isSaving = false;
        console.log('Error creating order',error);
        this._toastr.error('Error creating order', 'Error');
      }
    })
  }

  reloadCart() {
    this.orderService.initCartState()
  }
}
