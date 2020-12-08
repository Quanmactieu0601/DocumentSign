import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { SystemConfigComponent } from './system-config.component';
import { SystemConfigDetailComponent } from './system-config-detail.component';
import { SystemConfigUpdateComponent } from './system-config-update.component';
import { SystemConfigDeleteDialogComponent } from './system-config-delete-dialog.component';
import { systemConfigRoute } from './system-config.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(systemConfigRoute)],
  declarations: [SystemConfigComponent, SystemConfigDetailComponent, SystemConfigUpdateComponent, SystemConfigDeleteDialogComponent],
  entryComponents: [SystemConfigDeleteDialogComponent],
})
export class WebappSystemConfigModule {}
