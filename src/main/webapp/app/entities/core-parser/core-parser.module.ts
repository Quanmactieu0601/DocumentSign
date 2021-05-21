import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { CoreParserComponent } from './core-parser.component';
import { CoreParserDetailComponent } from './core-parser-detail.component';
import { coreParserRoute } from './core-parser.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(coreParserRoute)],
  declarations: [CoreParserComponent, CoreParserDetailComponent],
})
export class WebappCoreParserModule {}
