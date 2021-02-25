import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { VerifySignaturePdfComponent } from './verify-signature-pdf.component';
import { RouterModule } from '@angular/router';
import { verifySignaturePdfRoute } from 'app/verify/verify-signature-pdf/verify-signature-pdf.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignaturePdfRoute)],
  declarations: [VerifySignaturePdfComponent],
  providers: [],
  bootstrap: [VerifySignaturePdfComponent],
})
export class VerifySignaturePdfModule {}
