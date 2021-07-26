import { NgModule } from '@angular/core';
import { LoadingBarHttpClientModule } from '@ngx-loading-bar/http-client';
import { LoadingBarModule } from '@ngx-loading-bar/core';
import { BrowserModule } from '@angular/platform-browser';
import './vendor';
import { WebappSharedModule } from 'app/shared/shared.module';
import { WebappCoreModule } from 'app/core/core.module';
import { WebappAppRoutingModule } from './app-routing.module';
import { NgxDropzoneModule } from 'ngx-dropzone';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { ErrorComponent } from './layouts/error/error.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { LoginComponent } from './login/login.component';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { AngularDraggableModule } from 'angular2-draggable';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { ArchwizardModule } from 'angular-archwizard';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  imports: [
    BrowserModule,
    WebappSharedModule,
    WebappCoreModule,
    LoadingBarHttpClientModule,
    LoadingBarModule,
    NgxDropzoneModule,
    AngularDraggableModule,
    // jhipster-needle-angular-add-module JHipster will add new module here,
    WebappAppRoutingModule,
    BrowserAnimationsModule,
    NgxExtendedPdfViewerModule,
    ArchwizardModule,
    ToastrModule.forRoot(),
  ],
  declarations: [MainComponent, ErrorComponent, LoginComponent, HomeLayoutComponent],
  bootstrap: [MainComponent],
  providers: [NgbActiveModal],
})
export class WebappAppModule {}
