import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { saveAs } from 'file-saver';

@Component({
  selector: 'jhi-upload-signature-image',
  templateUrl: './upload-signature-image.component.html',
  styleUrls: ['./upload-signature-image.component.scss'],
})
export class UploadSignatureImageComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  successFile: any;
  listImageFiles: any;
  imageFileImport: any;
  progress = 0;
  fileInfos: Observable<any> = new Observable<any>();
  constructor(private certificateService: CertificateService, private toastService: ToastrService, private translate: TranslateService) {}

  ngOnInit(): void {}
  selectedSuccessFile(event: any): void {
    this.successFile = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastService.error('Dung lượng tệp tải lên phải nhỏ hơn 1MB');
      this.successFile = [];
    }
  }

  selectedImageFiles(event: any): void {
    this.listImageFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastService.error('Dung lượng tệp tải lên phải nhỏ hơn 1MB');
      this.listImageFiles = [];
    }
  }
  upload(): void {
    this.progress = 0;

    this.imageFileImport = {
      successFile: this.successFile,
      imageFiles: this.listImageFiles,
    };

    this.certificateService.uploadSignatureImage(this.successFile, this.listImageFiles).subscribe((response: any) => {
      if (response.type === 0) {
        this.progress = 100;
      } else {
        saveAs(new Blob([response.body], { type: 'text/plain;charset=utf-8' }), 'ErrorImageInsert.txt');
        this.transformVariable(true);
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
