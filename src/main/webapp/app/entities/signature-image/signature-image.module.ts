import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { SignatureImageComponent } from './signature-image.component';
import { SignatureImageDetailComponent } from './signature-image-detail.component';
import { SignatureImageUpdateComponent } from './signature-image-update.component';
import { SignatureImageDeleteDialogComponent } from './signature-image-delete-dialog.component';
import { signatureImageRoute } from './signature-image.route';
import { CertificateSignatureComponent } from 'app/entities/signature-image/certificate-signature-view/certificate-signature.component';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(signatureImageRoute)],
  declarations: [
    SignatureImageComponent,
    SignatureImageDetailComponent,
    SignatureImageUpdateComponent,
    SignatureImageDeleteDialogComponent,
    CertificateSignatureComponent,
  ],
  entryComponents: [SignatureImageDeleteDialogComponent],
})
export class WebappSignatureImageModule {}
