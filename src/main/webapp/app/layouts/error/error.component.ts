import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss'],
})
export class ErrorComponent implements OnInit, OnDestroy {
  errorMessage?: string;
  errorKey?: string;
  pageTitleMessage?: string;
  pageTitleKey?: string;
  langChangeSubscription?: Subscription;
  icon?: string;
  color?: string;

  constructor(private translateService: TranslateService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data.subscribe(routeData => {
      this.icon = routeData.icon;
      this.color = routeData.color;
      if (routeData.errorMessage) {
        this.errorKey = routeData.errorMessage;
        this.getErrorMessageTranslation();
      }
      this.pageTitleKey = routeData.pageTitle;
      this.getPageTitleMessageTranslation();
      this.langChangeSubscription = this.translateService.onLangChange.subscribe(() => {
        this.getErrorMessageTranslation();
        this.getPageTitleMessageTranslation();
      });
    });
  }

  ngOnDestroy(): void {
    if (this.langChangeSubscription) {
      this.langChangeSubscription.unsubscribe();
    }
  }

  private getErrorMessageTranslation(): void {
    this.errorMessage = '';
    if (this.errorKey) {
      this.translateService.get(this.errorKey).subscribe(translatedErrorMessage => (this.errorMessage = translatedErrorMessage));
    }
  }

  private getPageTitleMessageTranslation(): void {
    this.pageTitleMessage = '';
    if (this.pageTitleKey) {
      this.translateService.get(this.pageTitleKey).subscribe(translatedErrorMessage => (this.pageTitleMessage = translatedErrorMessage));
    }
  }
}
