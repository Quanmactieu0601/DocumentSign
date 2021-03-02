import { Component, OnInit } from '@angular/core';
import { ICertificate } from 'app/shared/model/certificate.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { SignatureImageService } from 'app/entities/signature-image/signature-image.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'jhi-imagesign',
  templateUrl: './certificate-signature.component.html',
  styleUrls: ['../signature-image.component.scss'],
})
export class CertificateSignatureComponent implements OnInit {
  certificate?: ICertificate;
  base64Img?: SafeResourceUrl;
  showAlert = false;
  fileName: any = this.translateService.instant('webappApp.signatureImage.showImageSign.chooseImage');
  imageFiles: any;
  results: any | null;
  imageUrl: any;

  constructor(
    public activeModal: NgbActiveModal,
    private translateService: TranslateService,
    private sanitizer: DomSanitizer,
    private signatureImageService: SignatureImageService,
    private toastrService: ToastrService
  ) {}

  getImage(id?: number | undefined): any {
    this.signatureImageService.getBase64(id).subscribe((res: any) => {
      if (res.body === '') {
        this.showAlert = true;
        this.toastrService.error(this.translateService.instant('webappApp.signatureImage.showImageSign.showAlert'));
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
    reader.readAsDataURL(this.imageFiles[0]);
    this.fileName = this.imageFiles[0].name;
  }

  save(): any {
    this.signatureImageService.saveImage(this.imageFiles, this.certificate?.id).subscribe((res: any) => {
      this.results = res.body;
      this.toastrService.success(this.translateService.instant('webappApp.signatureImage.showImageSign.success'));
    });
  }

  cancel(): void {
    this.activeModal.close(this.results);
  }
}
