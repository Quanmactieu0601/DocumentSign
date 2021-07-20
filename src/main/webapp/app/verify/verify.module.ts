import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { verifyRoute } from 'app/verify/verify.route';
import { VerifySignatureHashComponent } from 'app/verify/verify-signature-hash/verify-signature-hash.component';
import { VerifySignatureDocComponent } from 'app/verify/verify-signature-doc/verify-signature-doc.component';
import { VerifySignaturePdfComponent } from 'app/verify/verify-signature-pdf/verify-signature-pdf.component';
import { VerifySignatureRawComponent } from 'app/verify/verify-signature-raw/verify-signature-raw.component';
import { WebappSharedModule } from 'app/shared/shared.module';
import { NgxDropzoneModule } from 'ngx-dropzone';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(verifyRoute), NgxDropzoneModule],
  declarations: [VerifySignatureHashComponent, VerifySignatureDocComponent, VerifySignaturePdfComponent, VerifySignatureRawComponent],
})
export class VerifyModule {}
