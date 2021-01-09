import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { FormBuilder, Validators } from '@angular/forms';
import { ICertificate } from 'app/shared/model/certificate.model';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-password',
  templateUrl: './password.component.html',
})
export class PasswordComponent implements OnInit {
  certificate?: ICertificate;
  isAuthenOTP = false;
  changeCertPINForm = this.fb.group({
    currentPINInput: ['', [Validators.required]],
    newPINInput: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
    confirmPINInput: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
  });

  constructor(
    public activeModal: NgbActiveModal,
    private certificateService: CertificateService,
    private fb: FormBuilder,
    private toastService: ToastrService,
    private translate: TranslateService
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {}

  changePassword(): void {
    const serial = this.certificate?.serial;
    const currentPIN = this.changeCertPINForm.get(['currentPINInput'])!.value;
    const newPIN = this.changeCertPINForm.get(['newPINInput'])!.value;
    if (newPIN !== this.changeCertPINForm.get(['confirmPINInput'])!.value) {
      this.toastService.error(this.translate.instant('webappApp.certificate.changeCertPIN.error'));
    } else {
      this.certificateService.savePIN(serial, currentPIN, newPIN).subscribe((res: any) => {
        if (res.status !== 0) {
          this.toastService.error(res.msg);
        } else {
          this.toastService.success(this.translate.instant('webappApp.certificate.changeCertPIN.success'));
        }
      });
    }
  }
}
