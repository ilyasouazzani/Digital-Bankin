import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {
  username = '';
  constructor(private authService: AuthService) {}
  ngOnInit(): void { this.username = this.authService.getUsername(); }
  logout(): void { this.authService.logout(); }
}
