import { NgModule } from '@angular/core';
import { LoadingBarHttpClientModule } from '@ngx-loading-bar/http-client';
import { LoadingBarModule } from '@ngx-loading-bar/core';
import { BrowserModule } from '@angular/platform-browser';
import './vendor';
import { WebappSharedModule } from 'app/shared/shared.module';
import { WebappCoreModule } from 'app/core/core.module';
import { WebappAppRoutingModule } from './app-routing.module';
import { WebappHomeModule } from './home/home.module';
import { WebappEntityModule } from './entities/entity.module';

// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

@NgModule({
  imports: [
    BrowserModule,
    WebappSharedModule,
    WebappCoreModule,
    WebappHomeModule,
    LoadingBarHttpClientModule,
    LoadingBarModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    WebappEntityModule,
    WebappAppRoutingModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent],
})
export class WebappAppModule {}
