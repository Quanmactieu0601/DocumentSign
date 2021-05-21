import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { VerifySignaturePdfComponent } from './verify-signature-pdf.component';
import { RouterModule } from '@angular/router';
import { verifySignaturePdfRoute } from 'app/verify/verify-signature-pdf/verify-signature-pdf.route';
import { NgxDropzoneModule } from 'ngx-dropzone';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignaturePdfRoute), NgxDropzoneModule],
  declarations: [VerifySignaturePdfComponent],
  providers: [],
  bootstrap: [VerifySignaturePdfComponent],
})
export class VerifySignaturePdfModule {}
