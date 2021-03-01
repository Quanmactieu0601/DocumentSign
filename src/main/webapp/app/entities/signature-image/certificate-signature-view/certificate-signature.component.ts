import { Component, OnInit } from '@angular/core';
import { ICertificate } from 'app/shared/model/certificate.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { SignatureImageService } from 'app/entities/signature-image/signature-image.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-imagesign',
  templateUrl: './certificate-signature.component.html',
  styleUrls: ['../signature-image.component.scss'],
})
export class CertificateSignatureComponent implements OnInit {
  certificate?: ICertificate;
  base64Img?: SafeResourceUrl;
  showAlert = false;
  fileName: any = this.translate.instant('webappApp.signatureImage.showImageSign.chooseImage');
  imageFiles: any;

  constructor(
    public activeModal: NgbActiveModal,
    private translate: TranslateService,
    private sanitizer: DomSanitizer,
    private signatureImageService: SignatureImageService
  ) {}

  getImage(id?: number | undefined): any {
    this.signatureImageService.getBase64(id).subscribe((res: any) => {
      if (res.body === '') {
        this.showAlert = true;
      } else {
        this.base64Img = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64, ' + res.body);
      }
    });
  }

  ngOnInit(): void {
    this.getImage(this.certificate?.signatureImageId);
  }

  choose(_event: any): any {
    this.imageFiles = _event.target.files;
    // const reader = new FileReader();
    // reader.onload = (event: any) => {
    //   this.imageUrl = event.target.result;
    // }
    // reader.readAsDataURL(this.imageFiles);
    this.fileName = this.imageFiles[0].name;
  }

  save(): any {
    this.signatureImageService.saveImage(this.imageFiles, this.certificate?.id).subscribe((res: any) => {
      this.activeModal.close(res.body);
    });
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
