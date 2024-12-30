import {Component, inject, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import Keycloak from 'keycloak-js';
import {CookieService} from 'ngx-cookie-service';
import {NgIf} from '@angular/common';
import {OrderService} from './services/order.service';
import {UserStateService} from './services/user-state.service';
import {NgxIndexedDBService, ObjectStoreMeta} from 'ngx-indexed-db';

@Component({
  selector: 'app-root',
  imports: [
    RouterLink,
    NgIf,
    RouterOutlet

  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'e-com-ui';

  public readonly keycloak = inject(Keycloak);
  private readonly cookiesService = inject(CookieService);
  orderService = inject(OrderService);
  userStateService = inject(UserStateService);
  private dbService: NgxIndexedDBService = inject(NgxIndexedDBService);


  userFullName: string | undefined = '';

  private storeSchema: ObjectStoreMeta = {
    store: '',
    storeConfig: { keyPath: 'id', autoIncrement: false },
    storeSchema: [
      { name: 'id', keypath: 'id', options: {unique: true}},
      { name: 'name', keypath: 'name', options: { unique: false } },
      { name: 'description', keypath: 'description', options: { unique: false } },
      { name: 'price', keypath: 'price', options: { unique: false } },
      { name: 'quantityInCart', keypath: 'quantityInCart', options: { unique: false } },
      { name: 'quantityInStorage', keypath: 'quantityInStorage', options: { unique: false } }
    ]
  }

  ngOnInit() {
    if(this.keycloak.authenticated && this.keycloak.tokenParsed && this.keycloak.tokenParsed.preferred_username){
      this.storeSchema.store = this.keycloak.tokenParsed.preferred_username;
      this.dbService.createObjectStore(this.storeSchema)
        .then(() => console.log("Table added."))
        .catch(() => this.dbService.deleteDatabase().subscribe({
          next: () => this.dbService.createObjectStore(this.storeSchema).then(() => console.log("Table added.")),
          error: err => console.error("Error creating store",err)
        }))
    }
    this.userStateService.initUserState();
      if(this.keycloak.authenticated) {
        this.userFullName = this.keycloak.tokenParsed?.name?.toString();

        this.orderService.initCartState()

        console.log(this.orderService.shoppingCart);
      }
  }

  async logout() {
    this.keycloak.authenticated = false;
    await this.keycloak.logout({redirectUri: 'http://localhost:4200/', logoutMethod: 'POST'});
    this.cookiesService.deleteAll(
      '/realms/e-com-realm/',
      'http://localhost:4200')
  }

  async login() {
    await this.keycloak.login();
  }
}
