import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICertificate } from 'app/shared/model/certificate.model';
import { CertificateService } from './certificate.service';

@Component({
  templateUrl: './certificate-delete-dialog.component.html',
})
export class CertificateDeleteDialogComponent {
  certificate?: ICertificate;

  constructor(
    protected certificateService: CertificateService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.certificateService.delete(id).subscribe(() => {
      this.eventManager.broadcast('certificateListModification');
      this.activeModal.close();
    });
  }
}
