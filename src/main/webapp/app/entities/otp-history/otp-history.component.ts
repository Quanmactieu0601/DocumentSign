import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOtpHistory } from 'app/shared/model/otp-history.model';
import { OtpHistoryService } from './otp-history.service';
import { OtpHistoryDeleteDialogComponent } from './otp-history-delete-dialog.component';

@Component({
  selector: 'jhi-otp-history',
  templateUrl: './otp-history.component.html',
})
export class OtpHistoryComponent implements OnInit, OnDestroy {
  otpHistories?: IOtpHistory[];
  eventSubscriber?: Subscription;

  constructor(protected otpHistoryService: OtpHistoryService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.otpHistoryService.query().subscribe((res: HttpResponse<IOtpHistory[]>) => (this.otpHistories = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInOtpHistories();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IOtpHistory): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInOtpHistories(): void {
    this.eventSubscriber = this.eventManager.subscribe('otpHistoryListModification', () => this.loadAll());
  }

  delete(otpHistory: IOtpHistory): void {
    const modalRef = this.modalService.open(OtpHistoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.otpHistory = otpHistory;
  }
}
