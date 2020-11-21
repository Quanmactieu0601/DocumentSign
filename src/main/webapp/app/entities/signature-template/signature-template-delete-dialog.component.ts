import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISignatureTemplate } from 'app/shared/model/signature-template.model';
import { SignatureTemplateService } from './signature-template.service';

@Component({
  templateUrl: './signature-template-delete-dialog.component.html',
})
export class SignatureTemplateDeleteDialogComponent {
  signatureTemplate?: ISignatureTemplate;

  constructor(
    protected signatureTemplateService: SignatureTemplateService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.signatureTemplateService.delete(id).subscribe(() => {
      this.eventManager.broadcast('signatureTemplateListModification');
      this.activeModal.close();
    });
  }
}
