import {Component, inject} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import Keycloak from 'keycloak-js';
import {CookieService} from 'ngx-cookie-service';
import {NgIf} from '@angular/common';

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
export class AppComponent {
  title = 'e-com-ui';
  public readonly keycloak = inject(Keycloak);
  private readonly cookiesService = inject(CookieService);

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
