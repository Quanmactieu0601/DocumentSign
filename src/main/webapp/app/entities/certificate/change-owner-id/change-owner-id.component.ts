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
  pin: any;
  OwnerID: any;
  account: Account | null = null;
  timer: NodeJS.Timeout | undefined;
  changeOwnerIDForm = this.fb.group({
    OwnerID: ['', [Validators.minLength(4), Validators.maxLength(50), Validators.required]],
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
    this.userService.query().subscribe((res: any) => (this.users = res.body));
    this.getListOwnerID('', 0);
  }

  getListOwnerID(s: string, p: number): void {
    const data = {
      page: p,
      size: 20,
      sort: ['id,desc'],
    };
    if (p === 0) this.users = [];
    this.userService.query(data).subscribe((res: HttpResponse<ICertificate[]>) => {
      this.users.push(...(res.body || []));
    });
  }

  @HostListener('scroll', ['$event'])
  getMoreCert(e: any): void {
    if (e.target.scrollHeight === e.target.scrollTop + e.target.clientHeight) {
      this.getListOwnerID(this.OwnerID, ++this.page);
    }
  }

  selectOwnerID(OwnerID: string): void {
    this.changeOwnerIDForm.controls['OwnerID'].setValue(OwnerID);
    this.OwnerID = OwnerID;
  }

  filter(part: string): void {
    if (this.timer) {
      clearTimeout(this.timer);
    }
    this.timer = setTimeout(() => {
      this.page = 0;
      this.getListOwnerID(part, this.page);
    }, 1000);
  }
  changeCertPIN(): void {
    this.certificateService.changeOwnerId(this.OwnerID, this.certificate?.id).subscribe((res: any) => {
      if (res.status !== 0) {
        this.toastService.error(res.msg);
      } else {
        this.toastService.success(this.translate.instant('webappApp.certificate.changeOwnerID.success'));
        window.location.reload();
      }
    });
  }
}
