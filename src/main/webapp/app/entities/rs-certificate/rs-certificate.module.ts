import { NgModule } from '@angular/core';
import { WebappSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { rsCertificateRoute } from 'app/entities/rs-certificate/rs-certificate.route';

@NgModule({
  declarations: [],
  imports: [WebappSharedModule, RouterModule.forChild([rsCertificateRoute])],
})
export class RsCertificateModule {}
