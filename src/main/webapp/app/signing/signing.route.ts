import { Routes } from '@angular/router';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';
import { SigningOfficeInvisibleComponent } from 'app/signing/signing-office-invisible/signing-office-invisible.component';
import { SigningPdfVisibleComponent } from 'app/signing/signing-pdf-visible/signing-pdf-visible.component';
import { SignRawComponent } from 'app/signing/sign-raw/sign-raw.component';
import { XmlfileComponent } from 'app/signing/xmlfile/xmlfile.component';

export const signingRoute: Routes = [
  {
    path: '',
    component: HomeLayoutComponent,
    children: [
      {
        path: 'signing-office-invisible',
        component: SigningOfficeInvisibleComponent,
      },
      {
        path: 'signing-pdf-visible',
        component: SigningPdfVisibleComponent,
      },
      {
        path: 'signing-raw',
        component: SignRawComponent,
      },
      {
        path: 'signing-xml',
        component: XmlfileComponent,
      },
    ],
  },
];
