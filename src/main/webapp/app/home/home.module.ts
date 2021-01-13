import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { WebappAppModule } from 'app/app.module';
import { LoadingBarModule } from '@ngx-loading-bar/core';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild([HOME_ROUTE]), WebappAppModule, LoadingBarModule],
  declarations: [HomeComponent],
})
export class WebappHomeModule {}
