import {Component, inject, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {OrderPage} from '../../models/models';

@Component({
  selector: 'app-orders',
  imports: [],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit{

  private _http: HttpClient = inject(HttpClient);
  orders!: OrderPage;
  page: number = 0;
  size: number = 10;
  isLoading: boolean = false;
  isEmpty: boolean = true;
  searchParams: HttpParams = new HttpParams();

  ngOnInit() {
    this.getOrders(new HttpParams())
  }

  getOrders(params: HttpParams){
    this.isLoading = true;
    this._http.get<OrderPage>(`${environment.orderURL}/api/orders`, { params: params }).subscribe({
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
    this.page = --$event;
    this.appendSearchParam("page", this.page);
    this.getOrders(this.searchParams);
  }

  sizeChanged() {
    this.appendSearchParam("size", this.size);
    this.page = 0;
  }

  search(){

  }

}
