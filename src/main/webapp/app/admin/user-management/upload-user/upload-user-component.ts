import { Component, EventEmitter, Output } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { UserService } from 'app/core/user/user.service.ts';
import { HttpEventType, HttpResponse } from '@angular/common/http';

@Component({
  selector: 'jhi-upload-user',
  templateUrl: './upload-user-component.html',
})
export class UploadUserComponent {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  selectFiles: any;
  progress = 0;
  currentFile: any;
  constructor(private toastrService: ToastrService, private translateService: TranslateService, private userService: UserService) {}

  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastrService.error(this.translateService.instant('userManagement.alert.fail.sizeErr'));
      this.selectFiles = [];
    }
  }

  upload(): void {
    this.progress = 0;
    this.currentFile = this.selectFiles.item(0);
    this.userService.upload(this.currentFile).subscribe((res: any) => {
      if (res.type === HttpEventType.UploadProgress) {
        this.progress = Math.round((100 * res.loaded) / res.total);
      } else if (res instanceof HttpResponse) {
        if (res.body.status === 200) {
          this.toastrService.success(this.translateService.instant('userManagement.alert.success.uploaded'));
          this.transformVariable(true);
        } else if (res.body.status === 417) {
          this.toastrService.error(this.translateService.instant('userManagement.alert.fail.uploaded'));
        } else if (res.body.status === 400) {
          this.toastrService.error(res.body.msg);
        }
      }
    });
  }
  transformVariable(agreed: boolean): void {
    this.isUploadedSucessfully.emit(agreed);
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
}
