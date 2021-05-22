import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { LoadingBarModule } from '@ngx-loading-bar/core';
import { NotificationComponent } from './notification/notification.component';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild([HOME_ROUTE]), LoadingBarModule],
  declarations: [HomeComponent, NotificationComponent],
  exports: [NotificationComponent],
})
export class WebappHomeModule {}
