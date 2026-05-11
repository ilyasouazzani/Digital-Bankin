import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserProfile } from '../../models/auth.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  profile: UserProfile | null = null;
  passwordForm!: FormGroup;
  loadingProfile = false;
  loadingPassword = false;
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadProfile();
    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required, Validators.minLength(6)]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  loadProfile(): void {
    this.loadingProfile = true;
    this.authService.getProfile().subscribe({
      next: p => { this.profile = p; this.loadingProfile = false; },
      error: () => this.loadingProfile = false
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const np = form.get('newPassword')?.value;
    const cp = form.get('confirmPassword')?.value;
    return np === cp ? null : { mismatch: true };
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) return;
    this.loadingPassword = true;
    this.errorMessage = '';
    const { oldPassword, newPassword } = this.passwordForm.value;
    this.authService.changePassword(oldPassword, newPassword).subscribe({
      next: () => {
        this.successMessage = 'Mot de passe modifié avec succès !';
        this.loadingPassword = false;
        this.passwordForm.reset();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: err => {
        this.errorMessage = err.error?.error || 'Erreur lors du changement de mot de passe.';
        this.loadingPassword = false;
      }
    });
  }
}
