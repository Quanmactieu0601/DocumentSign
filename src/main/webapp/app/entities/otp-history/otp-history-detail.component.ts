import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOtpHistory } from 'app/shared/model/otp-history.model';

@Component({
  selector: 'jhi-otp-history-detail',
  templateUrl: './otp-history-detail.component.html',
})
export class OtpHistoryDetailComponent implements OnInit {
  otpHistory: IOtpHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ otpHistory }) => (this.otpHistory = otpHistory));
  }

  previousState(): void {
    window.history.back();
  }
}
