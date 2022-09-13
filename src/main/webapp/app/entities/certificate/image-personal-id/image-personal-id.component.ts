import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { ResponseBody } from 'app/shared/model/response-body';
import { FileDataUtil } from 'app/shared/util/file-data.util';

@Component({
  selector: 'jhi-image-personal-id',
  templateUrl: './image-personal-id.component.html',
  styleUrls: ['./image-personal-id.component.scss'],
})
export class ImagePersonalIdComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  successFile: any;
  listImageFiles: any;
  imageFileImport: any;
  progress = 0;
  fileInfos: Observable<any> = new Observable<any>();
  fileName: any = this.translate.instant('webappApp.certificate.chooseFile');
  signatureImageName: any = this.translate.instant('webappApp.certificate.chooseSignatureImage');
  constructor(
    private certificateService: CertificateService,
    private toastService: ToastrService,
    private translate: TranslateService,
    public activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {}

  selectedImageFiles(event: any): void {
    this.listImageFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastService.error('Dung lượng tệp tải lên phải nhỏ hơn 1MB');
      this.listImageFiles = [];
    }
    for (const value of this.listImageFiles) {
      this.signatureImageName += value.name + ', ';
    }
    this.signatureImageName = this.signatureImageName.substring(0, this.signatureImageName.length - 2);
  }
  upload(): void {
    this.progress = 0;

    this.imageFileImport = {
      successFile: this.successFile,
      imageFiles: this.listImageFiles,
    };

    this.certificateService.uploadSignatureImageByPersonalIdComponent(this.listImageFiles).subscribe((response: any) => {
      if (response.status === ResponseBody.SUCCESS) {
        const filename: string = 'ImageImportResult' + '.xlsx';
        saveAs(
          new Blob([FileDataUtil.base64ToArrayBuffer(response.data)], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          }),
          filename
        );
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
