import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BankAccountService } from '../../services/bank-account.service';

@Component({
  selector: 'app-operations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './operations.component.html'
})
export class OperationsComponent implements OnInit {
  operationForm!: FormGroup;
  operationType: 'DEBIT' | 'CREDIT' | 'TRANSFER' = 'CREDIT';
  accountId = '';
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private bankAccountService: BankAccountService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.accountId = params['accountId'] || '';
    });
    this.initForm();
  }

  initForm(): void {
    this.operationForm = this.fb.group({
      accountId:          [this.accountId, Validators.required],
      amount:             [null, [Validators.required, Validators.min(1)]],
      description:        ['', Validators.required],
      accountDestination: ['']
    });
  }

  setType(type: 'DEBIT' | 'CREDIT' | 'TRANSFER'): void {
    this.operationType = type;
    const destCtrl = this.operationForm.get('accountDestination');
    if (type === 'TRANSFER') destCtrl?.setValidators([Validators.required]);
    else destCtrl?.clearValidators();
    destCtrl?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.operationForm.invalid) return;
    this.loading = true; this.errorMessage = ''; this.successMessage = '';
    const { accountId, amount, description, accountDestination } = this.operationForm.value;

    let op$;
    if (this.operationType === 'DEBIT')
      op$ = this.bankAccountService.debit(accountId, amount, description);
    else if (this.operationType === 'CREDIT')
      op$ = this.bankAccountService.credit(accountId, amount, description);
    else
      op$ = this.bankAccountService.transfer(accountId, accountDestination, amount, description || 'Virement');

    op$.subscribe({
      next: () => {
        this.successMessage = `${this.operationType} effectué avec succès !`;
        this.loading = false;
        this.operationForm.reset({ accountId: this.accountId });
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Erreur lors de l\'opération';
        this.loading = false;
      }
    });
  }
}
