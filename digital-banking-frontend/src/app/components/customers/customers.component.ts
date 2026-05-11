import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, FormsModule],
  templateUrl: './customers.component.html'
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  customerForm!: FormGroup;
  editMode = false;
  selectedCustomer: Customer | null = null;
  searchKeyword = '';
  errorMessage = '';
  successMessage = '';
  loading = false;
  showModal = false;

  constructor(private customerService: CustomerService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCustomers();
  }

  initForm(customer?: Customer): void {
    this.customerForm = this.fb.group({
      name:  [customer?.name  || '', [Validators.required, Validators.minLength(2)]],
      email: [customer?.email || '', [Validators.required, Validators.email]]
    });
  }

  loadCustomers(): void {
    this.loading = true;
    this.customerService.getAllCustomers(this.searchKeyword).subscribe({
      next: data => { this.customers = data; this.loading = false; },
      error: () => { this.errorMessage = 'Erreur lors du chargement.'; this.loading = false; }
    });
  }

  openCreateModal(): void {
    this.editMode = false;
    this.selectedCustomer = null;
    this.initForm();
    this.showModal = true;
    this.errorMessage = '';
  }

  openEditModal(customer: Customer): void {
    this.editMode = true;
    this.selectedCustomer = customer;
    this.initForm(customer);
    this.showModal = true;
    this.errorMessage = '';
  }

  closeModal(): void {
    this.showModal = false;
    this.errorMessage = '';
  }

  onSubmit(): void {
    if (this.customerForm.invalid) return;
    const data: Customer = this.customerForm.value;
    if (this.editMode && this.selectedCustomer?.id) {
      this.customerService.updateCustomer(this.selectedCustomer.id, data).subscribe({
        next: () => {
          this.successMessage = 'Client mis à jour avec succès !';
          this.closeModal();
          this.loadCustomers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: err => this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour.'
      });
    } else {
      this.customerService.createCustomer(data).subscribe({
        next: () => {
          this.successMessage = 'Client créé avec succès !';
          this.closeModal();
          this.loadCustomers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: err => this.errorMessage = err.error?.message || 'Erreur lors de la création.'
      });
    }
  }

  deleteCustomer(id: number): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) return;
    this.customerService.deleteCustomer(id).subscribe({
      next: () => {
        this.successMessage = 'Client supprimé avec succès !';
        this.loadCustomers();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: err => this.errorMessage = err.error?.message || 'Erreur lors de la suppression.'
    });
  }

  onSearch(): void {
    this.loadCustomers();
  }
}
