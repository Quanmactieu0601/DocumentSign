import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { SignatureImageComponent } from './signature-image.component';
import { SignatureImageDetailComponent } from './signature-image-detail.component';
import { SignatureImageUpdateComponent } from './signature-image-update.component';
import { SignatureImageDeleteDialogComponent } from './signature-image-delete-dialog.component';
import { signatureImageRoute } from './signature-image.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(signatureImageRoute)],
  declarations: [
    SignatureImageComponent,
    SignatureImageDetailComponent,
    SignatureImageUpdateComponent,
    SignatureImageDeleteDialogComponent,
  ],
  entryComponents: [SignatureImageDeleteDialogComponent],
})
export class WebappSignatureImageModule {}
