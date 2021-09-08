import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICertPackage } from 'app/shared/model/cert-package.model';
import { CertPackageService } from './cert-package.service';

@Component({
  templateUrl: './cert-package-delete-dialog.component.html',
})
export class CertPackageDeleteDialogComponent {
  certPackage?: ICertPackage;

  constructor(
    protected certPackageService: CertPackageService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.certPackageService.delete(id).subscribe(() => {
      this.eventManager.broadcast('certPackageListModification');
      this.activeModal.close();
    });
  }
}
