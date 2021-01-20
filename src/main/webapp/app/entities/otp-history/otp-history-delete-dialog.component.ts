import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IOtpHistory } from 'app/shared/model/otp-history.model';
import { OtpHistoryService } from './otp-history.service';

@Component({
  templateUrl: './otp-history-delete-dialog.component.html',
})
export class OtpHistoryDeleteDialogComponent {
  otpHistory?: IOtpHistory;

  constructor(
    protected otpHistoryService: OtpHistoryService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.otpHistoryService.delete(id).subscribe(() => {
      this.eventManager.broadcast('otpHistoryListModification');
      this.activeModal.close();
    });
  }
}
