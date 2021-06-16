import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';

import { VerifySignatureXmlComponent } from './verify-signature-xml.component';
import { RouterModule } from '@angular/router';
import { verifySignatureXmlRoute } from 'app/verify/verify-signature-xml/verify-signature-xml.route';
import { NgxDropzoneModule } from 'ngx-dropzone';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifySignatureXmlRoute), NgxDropzoneModule],
  declarations: [VerifySignatureXmlComponent],
  providers: [],
  bootstrap: [VerifySignatureXmlComponent],
})
export class VerifySignatureXmlModule {}
