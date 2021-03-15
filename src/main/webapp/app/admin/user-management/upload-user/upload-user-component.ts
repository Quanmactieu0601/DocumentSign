import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { UserService } from 'app/core/user/user.service.ts';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { Subscription } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResponseBody } from 'app/shared/model/response-body';

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

  downLoadFileTemplate(): void {
    this.userService.downLoadTemplateFile().subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        saveAs(this.base64toBlob(res.data), 'Sample-User-File.xlsx');
        this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
      } else {
        this.toastrService.error(this.translateService.instant('webappApp.certificate.errorGenerateCsr'));
      }
    });
  }

  base64toBlob(base64Data: string): any {
    const sliceSize = 1024;
    const byteCharacters = atob(base64Data);
    const bytesLength = byteCharacters.length;
    const slicesCount = Math.ceil(bytesLength / sliceSize);
    const byteArrays = new Array(slicesCount);

    for (let sliceIndex = 0; sliceIndex < slicesCount; ++sliceIndex) {
      const begin = sliceIndex * sliceSize;
      const end = Math.min(begin + sliceSize, bytesLength);

      const bytes = new Array(end - begin);
      for (let offset = begin, i = 0; offset < end; ++i, ++offset) {
        bytes[i] = byteCharacters[offset].charCodeAt(0);
      }
      byteArrays[sliceIndex] = new Uint8Array(bytes);
    }
    return new Blob(byteArrays);
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
