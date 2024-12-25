import {Component, inject, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ProductPage} from '../../models/models';
import {NgForOf, NgIf} from '@angular/common';
import {environment} from '../../../environments/environment.development';
import {FormsModule} from '@angular/forms';
import {NgbAlert, NgbPagination} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-products',
  imports: [
    NgForOf,
    FormsModule,
    NgbPagination,
    NgbAlert,
    NgIf


  ],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {
  private _http: HttpClient = inject(HttpClient);
  products!: ProductPage;
  searchParams: HttpParams = new HttpParams();
  id!: string;
  name!: string;
  description!: string;
  minPrice!: number;
  maxPrice!: number;
  minQuantity!: number;
  maxQuantity!: number;
  page: number = 0;
  size: number = 10;
  isLoading: boolean = false;
  isEmpty: boolean = true;

  ngOnInit() {
    this.getProducts(new HttpParams())
  }

  search(): void {
    this.appendSearchParam("id", this.id);
    this.appendSearchParam("name", this.name);
    this.appendSearchParam("description", this.description);
    this.appendSearchParam("minPrice", this.minPrice);
    this.appendSearchParam("maxPrice", this.maxPrice);
    this.appendSearchParam("minQuantity", this.minQuantity);
    this.appendSearchParam("maxQuantity", this.maxQuantity);
    this.appendSearchParam("page", 0);
    this.appendSearchParam("size", this.size);

    this.getProducts(this.searchParams);
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

  getProducts(searchParams: HttpParams) {
    this.isLoading = true;
    this._http.get<ProductPage>(`${environment.productURL}/api/products/search`, { params: searchParams }).subscribe({
      next: data => {
        this.isEmpty = data.content.length === 0;
        this.isLoading = false;
        this.products = data;
        this.page = data.number + 1;
      }, error: error => {
        this.isLoading = false;
        console.log('Error fetching products',error);
      }
    })
  }

  pageChanged($event: number) {
    this.page = --$event;
    this.appendSearchParam("page", this.page);
    this.getProducts(this.searchParams);
  }

  sizeChanged() {
    this.appendSearchParam("size", this.size);
    this.page = 0;
  }
}
