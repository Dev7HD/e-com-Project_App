import {Component, inject, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {OrderPage} from '../../models/order-page.model';
import {Order} from '../../models/order.model';
import {OrderState} from '../../models/order-state.enum';
import {FormsModule} from '@angular/forms';
import {DatePipe, formatDate, NgForOf, NgIf} from '@angular/common';
import {NgbAlert, NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {ToastrService} from 'ngx-toastr';
import {UserStateService} from '../../services/user-state.service';

@Component({
  selector: 'app-orders',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    NgbAlert,
    NgbPagination,
    DatePipe
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit{

  private _http: HttpClient = inject(HttpClient);
  private _toastr: ToastrService = inject(ToastrService);
  userStateService: UserStateService = inject(UserStateService);

  orders!: OrderPage;
  page: number = 0;
  size: number = 10;
  isLoading: boolean = false;
  isEmpty: boolean = true;
  searchParams: HttpParams = new HttpParams();
  id!: string;
  orderState!: string;
  minPrice!: number;
  maxPrice!: number;
  minQuantity!: number;
  maxQuantity!: number;
  minItemQuantity!: number;
  maxItemQuantity!: number;

  ngOnInit() {
    if (this.userStateService.userState.role == 'ADMIN'){
      this.getOrders(new HttpParams())
    } else {
      this.getCustomerOrders()
    }
  }

  getCustomerOrders(){
    this.isLoading = true;
    this._http.get<OrderPage>(`${environment.orderURL}/api/orders/customer-orders`).subscribe({
      next: value => {
        this.isLoading = false;
        this.isEmpty = value.content.length === 0;
        this.orders = value
      }, error: err => {
        this.isLoading = false;
        console.error("Error fetching orders.", err)
      }
    })
  }

  getOrders(params: HttpParams){
    this.isLoading = true;
    this._http.get<OrderPage>(`${environment.orderURL}/api/orders/all`, { params: params }).subscribe({
      next: data => {
        this.isLoading = false;
        this.isEmpty = data.content.length === 0;
        this.orders = data;
      }, error: error => {
        this.isLoading = false;
        console.log('Error fetching orders',error);
      }
    })
  }

  private appendSearchParam(key: string, value: any): void {
    if (this.definedAndNotNull(value)) {
      this.searchParams = this.searchParams.has(key)
        ? this.searchParams.set(key, value)
        : this.searchParams.append(key, value);
    }
  }

  private definedAndNotNull(value: any): boolean {
    return value !== undefined && value !== null;
  }

  pageChanged($event: number) {
    this.page = $event;
    let currentPage = Number($event-1);
    this.appendSearchParam("page", currentPage);
    this.getOrders(this.searchParams);
  }

  sizeChanged() {
    this.appendSearchParam("size", this.size);
    if (this.page === 0) {
      this.getOrders(this.searchParams);
    } else {
      this.page = 0;
    }
  }

  search(){
    this.appendSearchParam("id", this.id);
    this.appendSearchParam("orderState", this.orderState);
    this.appendSearchParam("minPrice", this.minPrice);
    this.appendSearchParam("maxPrice", this.maxPrice);
    this.appendSearchParam("minQuantity", this.minQuantity);
    this.appendSearchParam("maxQuantity", this.maxQuantity);
    this.appendSearchParam("minItemQuantity", this.minItemQuantity);
    this.appendSearchParam("maxItemQuantity", this.maxItemQuantity);
    this.appendSearchParam("page", 0);
    this.appendSearchParam("size", this.size);

    this.getOrders(this.searchParams);
  }

  protected readonly formatDate = formatDate;
  protected readonly OrderState = OrderState;

  confirmOrder(order: Order) {
    this._http.patch(`${environment.orderURL}/api/orders/${order.id}/confirm`, {}, { responseType: 'text' }).subscribe({
      next: response => {
        console.log("response: " + response);
        this._toastr.success(response, 'Success');
        this.getOrders(this.searchParams)
      }, error: () => {
        this._toastr.error('Oops! Confirming order failed', 'Error');
      }
    })
  }

  deliverOrder(order: Order) {
    this._http.patch(`${environment.orderURL}/api/orders/${order.id}/deliver`, {}, { responseType: 'text' }).subscribe({
      next: response => {
        console.log("response: " + response);
        this._toastr.success(response, 'Success');
        this.getOrders(this.searchParams)
      }, error: () => {
        this._toastr.error('Oops! Delivering order failed', 'Error');
      }
    })
  }

  cancelOrder(order: Order) {
    this._http.patch(`${environment.orderURL}/api/orders/${order.id}/cancel`, {}, { responseType: 'text' }).subscribe({
      next: response => {
        console.log("response: " + response);
        this._toastr.success(response, 'Success');
        this.getOrders(this.searchParams)
      }, error: () => {
        this._toastr.error('Oops! Canceling order failed', 'Error');
      }
    })
  }

  reset() {
    this.searchParams = new HttpParams();
    this.id = String();
    this.orderState = String() ;
    this.minPrice = Number();
    this.maxPrice = Number() ;
    this.minQuantity = Number() ;
    this.maxQuantity = Number() ;
    this.minItemQuantity = Number() ;
    this.maxItemQuantity = Number() ;
    this.page = 0;
    this.size = 10;

    this.getOrders(this.searchParams);
  }
}
