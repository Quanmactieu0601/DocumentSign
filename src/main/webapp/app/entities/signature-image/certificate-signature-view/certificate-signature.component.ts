import { Component, OnInit } from '@angular/core';
import { ICertificate } from 'app/shared/model/certificate.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { SignatureImageService } from 'app/entities/signature-image/signature-image.service';

@Component({
  selector: 'jhi-imagesign',
  templateUrl: './certificate-signature.component.html',
})
export class CertificateSignatureComponent implements OnInit {
  certificate?: ICertificate;
  base64Img?: SafeResourceUrl;
  showAlert = false;

  imageUrl: any;
  imageFiles: any;

  constructor(public activeModal: NgbActiveModal, private sanitizer: DomSanitizer, private signatureImageService: SignatureImageService) {}

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
    const reader = new FileReader();
    reader.onload = (event: any) => {
      this.imageUrl = event.target.result;
    };
  }

  save(): any {
    this.signatureImageService.saveImage(this.imageFiles, this.certificate?.id).subscribe((res: any) => {
      console.error(res);
    });
    this.activeModal.close();
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
