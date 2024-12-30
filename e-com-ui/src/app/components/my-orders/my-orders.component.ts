import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OrderPage} from '../../models/order-page.model';
import {environment} from '../../../environments/environment.development';

@Component({
  selector: 'app-my-orders',
  imports: [],
  templateUrl: './my-orders.component.html',
  styleUrl: './my-orders.component.css'
})
export class MyOrdersComponent implements OnInit{
  private _http: HttpClient = inject(HttpClient)

  orders!: OrderPage;

  ngOnInit() {
    this._http.get<OrderPage>(`${environment.orderURL}/api/orders/customer-orders`).subscribe({
      next: value => {
        this.orders = value
      }, error: err => console.error("Error fetching orders.", err)
    })
  }

}
