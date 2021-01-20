import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IOtpHistory, OtpHistory } from 'app/shared/model/otp-history.model';
import { OtpHistoryService } from './otp-history.service';

@Component({
  selector: 'jhi-otp-history-update',
  templateUrl: './otp-history-update.component.html',
})
export class OtpHistoryUpdateComponent implements OnInit {
  isSaving = false;
  actionTimeDp: any;
  expireTimeDp: any;

  editForm = this.fb.group({
    id: [],
    userId: [],
    comId: [],
    secretKey: [],
    otp: [],
    actionTime: [],
    expireTime: [],
  });

  constructor(protected otpHistoryService: OtpHistoryService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ otpHistory }) => {
      this.updateForm(otpHistory);
    });
  }

  updateForm(otpHistory: IOtpHistory): void {
    this.editForm.patchValue({
      id: otpHistory.id,
      userId: otpHistory.userId,
      comId: otpHistory.comId,
      secretKey: otpHistory.secretKey,
      otp: otpHistory.otp,
      actionTime: otpHistory.actionTime,
      expireTime: otpHistory.expireTime,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const otpHistory = this.createFromForm();
    if (otpHistory.id !== undefined) {
      this.subscribeToSaveResponse(this.otpHistoryService.update(otpHistory));
    } else {
      this.subscribeToSaveResponse(this.otpHistoryService.create(otpHistory));
    }
  }

  private createFromForm(): IOtpHistory {
    return {
      ...new OtpHistory(),
      id: this.editForm.get(['id'])!.value,
      userId: this.editForm.get(['userId'])!.value,
      comId: this.editForm.get(['comId'])!.value,
      secretKey: this.editForm.get(['secretKey'])!.value,
      otp: this.editForm.get(['otp'])!.value,
      actionTime: this.editForm.get(['actionTime'])!.value,
      expireTime: this.editForm.get(['expireTime'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOtpHistory>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
