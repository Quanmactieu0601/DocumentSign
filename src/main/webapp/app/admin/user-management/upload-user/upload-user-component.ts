import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { UserService } from 'app/core/user/user.service.ts';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { AccountService } from '../../../core/auth/account.service';
import { Subscription } from 'rxjs';
import { Account } from '../../../core/user/account.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-upload-user',
  templateUrl: './upload-user-component.html',
  styleUrls: ['./upload-user.component.scss'],
})
export class UploadUserComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  selectFiles: any;
  progress = 0;
  currentFile: any;
  account: Account | null = null;
  fileName: any = this.translateService.instant('userManagement.chooseFile');
  authSubscription?: Subscription;
  constructor(
    public activeModal: NgbActiveModal,
    private toastrService: ToastrService,
    private translateService: TranslateService,
    private userService: UserService,
    private accountService: AccountService
  ) {}
  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }
  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastrService.error(this.translateService.instant('userManagement.alert.fail.sizeErr'));
      this.selectFiles = [];
    }
    const file = event.target.files[0];
    this.fileName = file.name;
  }

  upload(): void {
    this.progress = 0;
    this.currentFile = this.selectFiles.item(0);
    if (this.account != null) {
      this.userService.upload(this.currentFile).subscribe((res: any) => {
        if (res.type === HttpEventType.UploadProgress) {
          this.progress = Math.round((100 * res.loaded) / res.total);
        } else if (res instanceof HttpResponse) {
          if (res.body.status === 200) {
            this.toastrService.success(this.translateService.instant('userManagement.alert.success.uploaded'));
            this.cancel();
          } else if (res.body.status === 417) {
            this.toastrService.error(res.body.msg);
          }
        }
      });
    }
  }

  onInputClick = (event: any) => {
    const element = event.target as HTMLInputElement;
    element.value = '';
  };

  downLoadFileTemplate(): void {
    this.userService.downLoadTemplateFile().subscribe(
      res => {
        const bindData = [];
        bindData.push(res.data);
        const url = window.URL.createObjectURL(
          new Blob(bindData, { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        );
        const a = document.createElement('a');
        document.body.appendChild(a);
        a.setAttribute('style', 'display: none');
        a.setAttribute('target', 'blank');
        a.href = url;
        a.download = 'templateFile';
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
      },
      error => {
        console.error(error);
      }
    );
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
