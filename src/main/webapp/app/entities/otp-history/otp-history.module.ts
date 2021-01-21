import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { OtpHistoryComponent } from './otp-history.component';
import { OtpHistoryDetailComponent } from './otp-history-detail.component';
import { OtpHistoryUpdateComponent } from './otp-history-update.component';
import { OtpHistoryDeleteDialogComponent } from './otp-history-delete-dialog.component';
import { otpHistoryRoute } from './otp-history.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(otpHistoryRoute)],
  declarations: [OtpHistoryComponent, OtpHistoryDetailComponent, OtpHistoryUpdateComponent, OtpHistoryDeleteDialogComponent],
  entryComponents: [OtpHistoryDeleteDialogComponent],
})
export class WebappOtpHistoryModule {}
