import { Route } from '@angular/router';
import { RsCertificateComponent } from 'app/entities/rs-certificate/rs-certificate.component';
import { Authority } from 'app/shared/constants/authority.constants';

export const rsCertificateRoute: Route = {
  path: '',
  component: RsCertificateComponent,
  data: {
    pageTitle: '',
    authorities: [Authority.USER, Authority.ADMIN, Authority.SUPER_ADMIN],
    defaultSort: 'id,desc',
  },
};
