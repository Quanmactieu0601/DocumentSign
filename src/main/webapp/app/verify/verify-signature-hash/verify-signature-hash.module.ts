import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { VerifySignatureHashComponent } from './verify-signature-hash.component';
import { RouterModule } from '@angular/router';
import { verifySignatureHashRoute } from 'app/verify/verify-signature-hash/verify-signature-hash.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignatureHashRoute)],
  declarations: [VerifySignatureHashComponent],
  providers: [],
  bootstrap: [VerifySignatureHashComponent],
})
export class VerifySignatureHashModule {}
