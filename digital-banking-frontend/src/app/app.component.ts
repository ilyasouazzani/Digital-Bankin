import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  template: `
    @if (showNavbar) { <app-navbar></app-navbar> }
    <main [class.pt-2]="showNavbar">
      <router-outlet></router-outlet>
    </main>
  `
})
export class AppComponent {
  showNavbar = false;
  constructor(private authService: AuthService, private router: Router) {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((e: any) => {
      this.showNavbar = !e.url.includes('/login') && this.authService.hasToken();
    });
  }
}
