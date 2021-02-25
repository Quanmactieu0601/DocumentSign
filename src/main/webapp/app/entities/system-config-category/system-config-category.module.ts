import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { SystemConfigCategoryComponent } from './system-config-category.component';
import { SystemConfigCategoryDetailComponent } from './system-config-category-detail.component';
import { SystemConfigCategoryUpdateComponent } from './system-config-category-update.component';
import { SystemConfigCategoryDeleteDialogComponent } from './system-config-category-delete-dialog.component';
import { systemConfigCategoryRoute } from './system-config-category.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(systemConfigCategoryRoute)],
  declarations: [
    SystemConfigCategoryComponent,
    SystemConfigCategoryDetailComponent,
    SystemConfigCategoryUpdateComponent,
    SystemConfigCategoryDeleteDialogComponent,
  ],
  entryComponents: [SystemConfigCategoryDeleteDialogComponent],
})
export class WebappSystemConfigCategoryModule {}
