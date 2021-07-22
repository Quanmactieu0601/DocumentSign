import { Component, OnInit } from '@angular/core';
import { ICertificate } from 'app/shared/model/certificate.model';
import { FormBuilder, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-change-owner-id',
  templateUrl: './change-owner-id.component.html',
  styleUrls: ['./change-owner-id.component.scss'],
})
export class ChangeOwnerIdComponent implements OnInit {
  certificate?: ICertificate;
  isAuthenOTP = false;
  changeCertPINForm = this.fb.group({
    currentPINInput: [''],
    newPINInput: ['', [Validators.minLength(4), Validators.maxLength(50)]],
    confirmPINInput: ['', [Validators.minLength(4), Validators.maxLength(50)]],
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

  changeCertPIN(): void {
    const currentPIN = this.changeCertPINForm.get(['currentPINInput'])!.value;

    this.certificateService.changeOwnerId(currentPIN, this.certificate?.id).subscribe((res: any) => {
      if (res.status !== 0) {
        this.toastService.error(res.msg);
      } else {
        this.toastService.success(this.translate.instant('webappApp.certificate.changeCertPIN.success'));
      }
    });
  }
}
