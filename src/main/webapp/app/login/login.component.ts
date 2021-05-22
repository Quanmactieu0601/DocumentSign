import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { LoginService } from 'app/core/login/login.service';
import { Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmChangePasswordComponent } from 'app/account/confirm-change-password-first-login/confirm-change-password.component';

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

  constructor(
    private loginService: LoginService,
    private router: Router,
    private accountService: AccountService,
    private toastrService: ToastrService,
    private translateService: TranslateService,
    protected modalService: NgbModal
  ) {}

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
    const username = this.usernameEl?.nativeElement.value;
    this.loginService.login(this.form).subscribe(
      () => {
        this.router.navigate(['/home']);

        this.accountService.isFirstLogin(username).subscribe((response: any) => {
          if (response === true) {
            this.modalService.open(ConfirmChangePasswordComponent, { size: '300', backdrop: 'static' });
          }
        });
        if (
          this.router.url === '/account/register' ||
          this.router.url.startsWith('/account/activate') ||
          this.router.url.startsWith('/account/reset/')
        ) {
          this.router.navigate(['']);
        }
      },
      () => this.toastrService.error(this.translateService.instant('login.messages.error.authentication'))
    );
  }
}
