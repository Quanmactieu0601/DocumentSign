import { Route } from '@angular/router';
import { RsCertificateComponent } from 'app/entities/rs-certificate/rs-certificate.component';

export const rsCertificateRoute: Route = {
  path: '',
  component: RsCertificateComponent,
  data: {
    pageTitle: '',
  },
};
