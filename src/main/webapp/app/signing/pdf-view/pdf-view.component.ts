import { Component, ElementRef, EventEmitter, Input, OnInit, Output, Renderer2, ViewChild } from '@angular/core';
import { SigningService } from 'app/core/signing/signing.service';
import { FormBuilder, Validators } from '@angular/forms';
import * as $ from 'jquery';
import 'jquery-ui/ui/widgets/draggable.js';
import { PdfViewerComponent } from 'ng2-pdf-viewer';
import { ResponseBody } from 'app/shared/model/response-body';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SignatureListComponent } from 'app/signing/pdf-view/signature-list/signature-list.component';
import { AccountService } from 'app/core/auth/account.service';
// import * as PDFJS from "pdfjs-dist";
(window as any).pdfWorkerSrc = '/assets/pdfjs/pdf.worker.js';

@Component({
  selector: 'jhi-pdf-view',
  templateUrl: './pdf-view.component.html',
  styleUrls: ['./pdf-view.component.scss'],
})
export class PdfViewComponent implements OnInit {
  @ViewChild(PdfViewerComponent) private pdfComponent: PdfViewerComponent | undefined;
  @ViewChild('serialElement') serialElement: ElementRef | undefined;
  @Input() pdfSrc = '';
  @Input() imageSrc: any;
  @Input() serial: any;
  @Input() pin: any;
  @Output() cancelEvent = new EventEmitter();
  @Output() signEvent = new EventEmitter<any>();

  signingForm = this.fb.group({
    serial: ['540110000b4525650231e39369660895', Validators.required],
    pin: ['079073009568', Validators.required],
  });

  renderText = true;
  originalSize = true;
  fitToPage = false;
  showAll = true;
  autoresize = false;
  showBorders = true;
  renderTextMode = 1;
  rotation = 0;
  zoom = 1;
  pdfQuery = '';
  totalPages!: number;
  heightPage = 900;
  modalRef: NgbModalRef | undefined;
  showMessageSerialRequired = false;

  constructor(
    private signingService: SigningService,
    private fb: FormBuilder,
    private elementRef: ElementRef,
    private renderer: Renderer2,
    private modalService: NgbModal,
    protected accountService: AccountService
  ) {}

  ngOnInit(): void {}

  // Event for search operation
  searchQueryChanged(newQuery: any): void {
    if (newQuery !== this.pdfQuery) {
      this.pdfQuery = newQuery;
      this.pdfComponent!.pdfFindController.executeCommand('find', {
        query: this.pdfQuery,
        highlightAll: true,
      });
    } else {
      this.pdfComponent!.pdfFindController.executeCommand('findagain', {
        query: this.pdfQuery,
        highlightAll: true,
      });
    }
  }

  // onFileSelected(): void {
  //   const $pdf: any = document.querySelector('#file');
  //   if (typeof FileReader !== 'undefined') {
  //     const reader = new FileReader();
  //
  //     reader.onload = (e: any) => {
  //       this.pdfSrc = e.target.result;
  //     };
  //     reader.readAsArrayBuffer($pdf.files[0]);
  //     this.renderTextMode = 1;
  //   }
  // }

  callBackFn(event: any): void {
    console.warn('callBackFn', event);
    // Setting total number of pages
    this.totalPages = event._pdfInfo.numPages;
    const element = document.getElementsByClassName('pdfViewer')[0];
    const child = document.createElement('img');
    child.setAttribute('id', 'signature-box');
    child.setAttribute('src', this.imageSrc);
    (child as HTMLElement).style.backgroundColor = 'red';
    (child as HTMLElement).style.position = 'absolute';
    (child as HTMLElement).style.top = `${this.heightPage}px`;
    (child as HTMLElement).style.left = '220px';
    (child as HTMLElement).style.zIndex = '9';
    (child as HTMLElement).style.background = 'transparent';
    // (child as HTMLElement).style.width = '256px';
    // (child as HTMLElement).style.height = '65px';
    // // this.renderer.setAttribute(child, 'ngDraggable','');
    this.renderer.appendChild(element, child);
  }

  pageRendered(event: any): void {
    console.warn('pageRendered', event);
    this.setSignatureInPage(this.renderTextMode);
  }

  setSignatureInPage(numberPage: any): void {
    const pdfPage = document.getElementsByClassName('page')[Number(numberPage) - 1] as HTMLElement;
    const sig = document.getElementById('signature-box');
    const dpi = 96;

    const page = this.pdfComponent!.pdfViewer._pages[Number(numberPage) - 1];
    ($('#signature-box') as any).draggable({
      containment: pdfPage,
      drag(): void {
        const boundX = pdfPage.offsetLeft;
        const boundY = pdfPage.offsetTop;
        const top = sig!.offsetTop;
        const left = sig!.offsetLeft;
        const xPos = Math.floor(((left - boundX - 9) / dpi) * 72);

        const h = Math.floor((sig!.offsetHeight / dpi) * 72);
        const test = page;
        const yPos =
          Math.ceil(test.pdfPage.getViewport(Number(numberPage) - 1).viewBox[3]) - Math.ceil(((top - boundY - 9) / dpi) * 72) - h;

        $('#xPos').text(xPos);
        $('#yPos').text(yPos);
      },

      stop(): void {},
    });

    if (Number(numberPage) === 1) {
      $('#signature-box').animate(
        {
          left: 300,
          top: 20,
        },
        0
      );
    } else {
      $('#signature-box').animate(
        {
          left: 300,
          top: Number(numberPage) * this.heightPage,
        },
        0
      );
    }
    this.getPosition();
  }

  getPosition(): void {
    const pdfPage = document.getElementsByClassName('page')[Number(this.renderTextMode) - 1] as HTMLElement;
    const sig = document.getElementById('signature-box');
    const dpi = 96;

    const boundX = pdfPage.offsetLeft;
    const boundY = pdfPage.offsetTop;
    const top = sig!.offsetTop;
    const left = sig!.offsetLeft;
    const xPos = Math.floor(((left - boundX - 9) / dpi) * 72);

    const h = Math.floor((sig!.offsetHeight / dpi) * 72);
    const yPos = 791 - Math.ceil(((top - boundY - 10) / dpi) * 72) - h;
    $('#xPos').text(xPos);
    $('#yPos').text(yPos);
  }

  sign(): void {
    const base64PdfContent = this.arrayBufferToBase64(this.pdfSrc);
    const request = {
      tokenInfo: { serial: this.serial, pin: this.pin },
      signingRequestContents: [
        {
          data: base64PdfContent.toString().replace('data:application/pdf;base64,', ''),
          location: {
            visibleX: $('#xPos').text(),
            visibleY: $('#yPos').text(),
            visibleWidth: Math.floor(($('#signature-box')[0].offsetWidth / 96) * 72),
            visibleHeight: Math.floor(($('#signature-box')[0].offsetHeight / 96) * 72),
          },
          extraInfo: { pageNum: Number(this.renderTextMode) },
          imageSignature: this.imageSrc.replace('data:image/jpeg;base64,', ''),
        },
      ],
    };

    this.signingService.signPdf(request).subscribe((res: ResponseBody) => {
      const byteArray = this.base64ToArrayBuffer(res);
      // saveAs(new Blob([byteArray], { type: 'application/pdf' }), Date.now().toString());
      this.signEvent.emit(byteArray);
    });
  }

  base64ToArrayBuffer(base64: any): ArrayBuffer {
    const binaryString = window.atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  cancel(): void {
    this.cancelEvent.emit();
  }

  arrayBufferToBase64(buffer: any): string {
    return btoa(new Uint8Array(buffer).reduce((data, byte) => data + String.fromCharCode(byte), ''));
  }

  openModalTemplateList(): void {
    if (this.signingForm.get('serial')?.invalid) {
      this.serialElement?.nativeElement.focus();
      this.showMessageSerialRequired = true;
      return;
    }

    this.modalRef = this.modalService.open(SignatureListComponent, { size: 'md' });
    this.accountService.identity(false).subscribe(res => {
      this.modalRef!.componentInstance.userId = res?.id;
    });
  }

  textLayerRendered(event: any): void {
    console.warn('textLayerRendered', event);
  }
  onError(event: any): void {
    console.warn('onError', event);
  }
  onProgress(event: any): void {
    console.warn('onProgress', event);
  }
}
