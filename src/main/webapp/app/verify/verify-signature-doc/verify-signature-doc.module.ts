import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { RouterModule } from '@angular/router';
import { VerifySignatureDocComponent } from 'app/verify/verify-signature-doc/verify-signature-doc.component';
import { verifySignatureDocRoute } from 'app/verify/verify-signature-doc/verify-signature-doc.route';
import { NgxDropzoneModule } from 'ngx-dropzone';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignatureDocRoute), NgxDropzoneModule],
  declarations: [VerifySignatureDocComponent],
  providers: [],
  bootstrap: [VerifySignatureDocComponent],
})
export class VerifySignatureDocModule {}
