import { Component, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'jhi-signing',
  templateUrl: './signing.component.html',
  styleUrls: ['./signing.component.scss'],
})
export class SigningComponent implements OnInit {
  FileToSign: any = null;
  srcPdfResult: any;
  imageSrc: any;
  serial: any;
  pin: any;

  @ViewChild('wizzard') wizzard: any;
  constructor(private sanitizer: DomSanitizer) {}

  ngOnInit(): void {}

  validateFileInput(FileToSign: File): any {
    if (typeof FileReader !== 'undefined') {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.FileToSign = e.target.result;
      };

      if (FileToSign) reader.readAsArrayBuffer(FileToSign);
      else this.FileToSign = FileToSign;
    }
  }

  cancel(): void {
    this.wizzard.goToPreviousStep();
  }
  signResult(signedFile: any): void {
    if (signedFile) {
      this.wizzard.goToNextStep();
      this.srcPdfResult = signedFile;
    }
  }

  nextStep(content: any): void {
    this.imageSrc = content.signatureImage;
    this.serial = content.signingForm.get('serial').value;
    this.pin = content.signingForm.get('pin').value;
    this.wizzard.goToNextStep();
  }
}
