import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISignatureImage } from 'app/shared/model/signature-image.model';
import { SignatureImageService } from './signature-image.service';

@Component({
  templateUrl: './signature-image-delete-dialog.component.html',
})
export class SignatureImageDeleteDialogComponent {
  signatureImage?: ISignatureImage;

  constructor(
    protected signatureImageService: SignatureImageService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.signatureImageService.delete(id).subscribe(() => {
      this.eventManager.broadcast('signatureImageListModification');
      this.activeModal.close();
    });
  }
}
