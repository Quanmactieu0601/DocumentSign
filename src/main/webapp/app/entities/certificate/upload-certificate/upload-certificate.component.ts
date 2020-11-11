import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-upload-certificate',
  templateUrl: './upload-certificate.component.html',
  styleUrls: ['./upload-certificate.component.scss'],
})
export class UploadCertificateComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  fileInfos: Observable<any> = new Observable<any>();
  constructor(private certificateService: CertificateService, private toastService: ToastrService, private translate: TranslateService) {}

  ngOnInit(): void {}
  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastService.error('Dung lượng tệp tải lên phải nhỏ hơn 1MB');
      this.selectedFiles = [];
    }
  }
  upload(): void {
    this.progress = 0;

    this.currentFile = this.selectedFiles.item(0);

    this.certificateService.upload(this.currentFile).subscribe((res: any) => {
      if (res.type === HttpEventType.UploadProgress) {
        this.progress = Math.round((100 * res.loaded) / res.total);
      } else if (res instanceof HttpResponse) {
        if (res.body.status === 200) {
          this.toastService.success(this.translate.instant('userManagement.alert.success.uploaded'));
          this.transformVariable(true);
        } else if (res.body.status === 417) {
          this.toastService.error(this.translate.instant('userManagement.alert.fail.uploaded'));
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
}
