import { Component, OnInit } from '@angular/core';
import { MenuToggleService } from 'app/shared/services/menu-toggle.service';
import { LoginService } from 'app/core/login/login.service';
import { JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService } from 'ngx-webstorage';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { Router } from '@angular/router';
import { LANGUAGES } from 'app/core/language/language.constants';
import { Account } from 'app/core/user/account.model';

@Component({
  selector: 'jhi-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss'],
})
export class TopbarComponent implements OnInit {
  currentAccount?: Account | null | undefined;
  languages = LANGUAGES;
  constructor(
    private menuToggle: MenuToggleService,
    private loginService: LoginService,
    private languageService: JhiLanguageService,
    private sessionStorage: SessionStorageService,
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private profileService: ProfileService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.currentAccount = account;
    });
    // this.currentAccount = this.accountService.getCurrentLoggedAccount();
  }

  toggleSideBar(): void {
    this.menuToggle.toggleSideBar();
  }

  changeLanguage(languageKey: string): void {
    this.sessionStorage.store('locale', languageKey);
    this.languageService.changeLanguage(languageKey);
  }

  getCurrentLanguage(): string {
    return this.languageService.getCurrentLanguage();
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
}
