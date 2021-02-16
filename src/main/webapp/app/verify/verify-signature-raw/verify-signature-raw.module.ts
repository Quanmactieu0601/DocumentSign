import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { VerifySignatureRawComponent } from './verify-signature-raw.component';
import { RouterModule } from '@angular/router';
import {verifySignatureRawRoute} from "app/verify/verify-signature-raw/verify-signature-raw.route";

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignatureRawRoute)],
  declarations: [VerifySignatureRawComponent],
  providers: [],
  bootstrap: [VerifySignatureRawComponent],
})
export class VerifySignatureRawModule {}
