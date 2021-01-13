import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { LoginService } from 'app/core/login/login.service';
import { Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, AfterViewInit {
  // @ViewChild('taxcode', { read: ElementRef, static: false }) taxcodeEl?: ElementRef;
  @ViewChild('username', { read: ElementRef, static: false }) usernameEl?: ElementRef;

  form: any = {};
  isLoggedIn = false;
  roles: string[] = [];

  constructor(private loginService: LoginService, private router: Router, private accountService: AccountService) {}

  ngAfterViewInit(): void {
    if (this.usernameEl) {
      this.usernameEl.nativeElement.focus();
    }
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe(data => {
      if (data !== null) this.router.navigate(['/home']);
    });
    this.form.rememberMe = false;
  }

  onSubmit(): void {
    // const returnUrl = this.stateStorageService.getUrl() || '/home';
    // this.stateStorageService.clearUrl();
    // this.authService.login(this.form).subscribe(
    //   data => {
    //     this.tokenStorage.store(data, this.form.rememberMe);
    //     this.isLoggedIn = true;
    //     this.roles = this.tokenStorage.getUser().roles;
    //     this.toastr.success(this.translate.instant('login.alert.success'), this.translate.instant('login.alert.title'));
    //     this.router.navigate([returnUrl]);
    //   },
    //   err => {
    //     this.toastr.error(this.translate.instant('login.alert.failure'), this.translate.instant('login.alert.title'));
    //     console.log(err);
    //   }
    // );

    this.loginService.login(this.form).subscribe(
      () => {
        // this.authenticationError = false;
        this.router.navigate(['/home']);
        if (
          this.router.url === '/account/register' ||
          this.router.url.startsWith('/account/activate') ||
          this.router.url.startsWith('/account/reset/')
        ) {
          this.router.navigate(['']);
        }
      },
      () =>
        alert('fail')
        // this.authenticationError = true
    );
  }
}
