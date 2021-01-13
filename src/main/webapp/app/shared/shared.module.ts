import { NgModule } from '@angular/core';
import { WebappSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { PageRibbonComponent } from 'app/layouts/profiles/page-ribbon.component';
import { NavbarComponent } from 'app/layouts/navbar/navbar.component';
import { TopbarComponent } from 'app/layouts/topbar/topbar.component';
import { FooterComponent } from 'app/layouts/footer/footer.component';

@NgModule({
  imports: [WebappSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    PageRibbonComponent,
    NavbarComponent,
    TopbarComponent,
    FooterComponent,
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    WebappSharedLibsModule,
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    PageRibbonComponent,
    NavbarComponent,
    TopbarComponent,
    FooterComponent,
  ],
})
export class WebappSharedModule {}
