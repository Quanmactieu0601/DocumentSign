import { Component, ElementRef, OnInit, Sanitizer, ViewChild } from '@angular/core';
import { doc } from 'prettier';
import printer = doc.printer;
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'jhi-signing',
  templateUrl: './signing.component.html',
  styleUrls: ['./signing.component.scss'],
})
export class SigningComponent implements OnInit {
  FileToSign: any = null;
  srcPdfResult: any;

  @ViewChild('wizzard') wizzard: any;
  constructor(private sanitizer: DomSanitizer) {}

  ngOnInit(): void {}
  finishFunction(): void {}

  validateFileInput(FileToSign: File): any {
    if (typeof FileReader !== 'undefined') {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.FileToSign = e.target.result;
      };

      if (FileToSign) reader.readAsDataURL(FileToSign);
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
}
