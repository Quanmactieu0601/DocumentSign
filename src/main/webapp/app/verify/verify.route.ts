import { Routes } from '@angular/router';
import { VerifySignaturePdfComponent } from 'app/verify/verify-signature-pdf/verify-signature-pdf.component';
import { VerifySignatureDocComponent } from 'app/verify/verify-signature-doc/verify-signature-doc.component';
import { VerifySignatureRawComponent } from 'app/verify/verify-signature-raw/verify-signature-raw.component';
import { VerifySignatureHashComponent } from 'app/verify/verify-signature-hash/verify-signature-hash.component';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';

export const verifyRoute: Routes = [
  {
    path: '',
    component: HomeLayoutComponent,
    children: [
      {
        path: 'verify-signature-pdf',
        component: VerifySignaturePdfComponent,
      },
      {
        path: 'verify-signature-doc',
        component: VerifySignatureDocComponent,
      },
      {
        path: 'verify-signature-raw',
        component: VerifySignatureRawComponent,
      },
      {
        path: 'verify-signature-hash',
        component: VerifySignatureHashComponent,
      },
    ],
  },
];
