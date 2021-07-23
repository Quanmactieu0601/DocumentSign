import { Component, HostListener, OnInit } from '@angular/core';
import { ICertificate } from 'app/shared/model/certificate.model';
import { FormBuilder, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { IUser, User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'jhi-change-owner-id',
  templateUrl: './change-owner-id.component.html',
  styleUrls: ['./change-owner-id.component.scss'],
})
export class ChangeOwnerIdComponent implements OnInit {
  certificate?: ICertificate;
  isAuthenOTP = false;
  users: User[] = [];
  page = 0;
  timer: NodeJS.Timeout | undefined;
  changeOwnerIDForm = this.fb.group({
    OwnerID: ['', [Validators.minLength(4), Validators.maxLength(50)]],
  });

  constructor(
    private userService: UserService,
    public activeModal: NgbActiveModal,
    private certificateService: CertificateService,
    private fb: FormBuilder,
    private toastService: ToastrService,
    private translate: TranslateService
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {
    this.userService
      .query({
        page: this.page,
        size: 500,
        sort: ['id,desc'],
      })
      .subscribe((res: any) => (this.users = res.body));
  }

  changeCertPIN(): void {
    const currentPIN = this.changeOwnerIDForm.get(['OwnerID'])!.value;
    this.certificateService.changeOwnerId(currentPIN, this.certificate?.id).subscribe((res: any) => {
      if (res.status !== 0) {
        this.toastService.error(res.msg);
      } else {
        this.toastService.success(this.translate.instant('webappApp.certificate.changeOwnerID.success'));
        window.location.reload();
      }
    });
  }
}
