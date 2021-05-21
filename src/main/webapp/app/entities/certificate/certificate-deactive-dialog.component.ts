import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICertificate } from 'app/shared/model/certificate.model';
import { CertificateService } from './certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  templateUrl: './certificate-deactive-dialog.component.html',
})
export class CertificateDeactiveDialogComponent {
  certificate?: ICertificate;

  constructor(
    protected certificateService: CertificateService,
    public activeModal: NgbActiveModal,
    private toastService: ToastrService,
    private translate: TranslateService
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDeactive(id: number): void {
    this.certificateService.updateActiveStatus(id).subscribe((res: any) => {
      this.activeModal.close(res.ok);
      this.toastService.success(this.translate.instant('webappApp.certificate.confirmDeactive.success'));
    });
  }
}
