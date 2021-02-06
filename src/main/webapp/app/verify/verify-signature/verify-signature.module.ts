import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { VerifySignatureComponent } from './verify-signature.component';
import { RouterModule } from '@angular/router';
import { verifySignatureRoute } from 'app/verify/verify-signature/verify-signature.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignatureRoute)],
  declarations: [VerifySignatureComponent],
  providers: [],
  bootstrap: [VerifySignatureComponent],
})
export class VerifySignatureModule {}
