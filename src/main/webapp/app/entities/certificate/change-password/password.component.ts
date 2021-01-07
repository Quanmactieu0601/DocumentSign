import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { FormBuilder, Validators } from '@angular/forms';
import { ICertificate } from 'app/shared/model/certificate.model';

@Component({
  selector: 'jhi-password',
  templateUrl: './password.component.html',
  styleUrls: ['./password.component.scss'],
})
export class PasswordComponent implements OnInit {
  certificate?: ICertificate;
  isAuthenOTP?: boolean;
  error = false;
  success = false;
  doNotMatch = false;
  passwordForm = this.fb.group({
    currentPasswordInput: ['abc123@', [Validators.required]],
    newPasswordInput: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
    confirmPasswordInput: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
  });

  constructor(public activeModal: NgbActiveModal, private certificateService: CertificateService, private fb: FormBuilder) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {}

  changePassword(): void {
    this.error = false;
    this.success = false;
    this.doNotMatch = false;

    const newPassword = this.passwordForm.get(['newPasswordInput'])!.value;

    if (newPassword !== this.passwordForm.get(['confirmPasswordInput'])!.value) {
      this.doNotMatch = true;
    } else {
      this.certificateService.savePassword(newPassword, this.passwordForm.get(['currentPasswordInput'])!.value).subscribe(
        () => (this.success = true),
        () => (this.error = true)
      );
    }
  }
}
