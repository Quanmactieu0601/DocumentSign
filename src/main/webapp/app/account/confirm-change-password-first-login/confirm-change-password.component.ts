import { Component, ElementRef, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from 'app/core/auth/account.service';
import { Router } from '@angular/router';
import { IUser } from 'app/core/user/user.model';

@Component({
  templateUrl: './confirm-change-password.component.html',
})
export class ConfirmChangePasswordComponent {
  @ViewChild('username', { read: ElementRef, static: false }) usernameEl?: ElementRef;

  user?: IUser;
  constructor(protected accountService: AccountService, public activeModal: NgbActiveModal, private router: Router) {}

  close(): void {
    this.activeModal.dismiss();
  }

  cancel(): void {
    this.accountService.setDefaultChangePassword().subscribe();
    this.activeModal.dismiss();
  }

  toChangePassword(): void {
    this.router.navigate(['/account/password']);
    this.activeModal.close();
  }
}
