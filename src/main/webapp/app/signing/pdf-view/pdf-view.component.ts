import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { SigningService } from 'app/core/signing/signing.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as $ from 'jquery';
import 'jquery-ui/ui/widgets/draggable.js';
import { PdfViewerComponent } from 'ng2-pdf-viewer';
import { saveAs } from 'file-saver';

@Component({
  selector: 'jhi-pdf-view',
  templateUrl: './pdf-view.component.html',
  styleUrls: ['./pdf-view.component.scss'],
})
export class PdfViewComponent implements OnInit {
  @ViewChild(PdfViewerComponent) private pdfComponent: PdfViewerComponent | undefined;
  @Input() pdfSrc = '';
  @Output() cancelEvent = new EventEmitter();
  @Output() signEvent = new EventEmitter<boolean>();
  title = 'angular-pdf-viewer-app';
  signingForm = this.fb.group({
    serial: ['', Validators.required],
    pin: ['', Validators.required],
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
  base64Content = '';

  certificateInfoForm = this.fb.group({
    serial: ['', [Validators.required]],
    pin: ['', [Validators.required]],
  });

  constructor(
    private signingService: SigningService,
    private fb: FormBuilder,
    private elementRef: ElementRef,
    private renderer: Renderer2
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

  onFileSelected(): void {
    const $pdf: any = document.querySelector('#file');
    if (typeof FileReader !== 'undefined') {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        this.pdfSrc = e.target.result;
      };
      reader.readAsDataURL($pdf.files[0]);
      this.renderTextMode = 1;
    }
  }

  callBackFn(event: any): void {
    console.warn('callBackFn', event);
    // Setting total number of pages
    this.totalPages = event._pdfInfo.numPages;
    const element = document.getElementsByClassName('pdfViewer')[0];
    const child = document.createElement('div');
    child.setAttribute('id', 'signature-box');
    (child as HTMLElement).style.backgroundColor = 'red';
    (child as HTMLElement).style.position = 'absolute';
    (child as HTMLElement).style.top = `${this.heightPage}px`;
    (child as HTMLElement).style.left = '220px';
    (child as HTMLElement).style.zIndex = '9';
    (child as HTMLElement).style.width = '256px';
    (child as HTMLElement).style.height = '65px';
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

    ($('#signature-box') as any).draggable({
      containment: pdfPage,
      drag(): void {
        const boundX = pdfPage.offsetLeft;
        const boundY = pdfPage.offsetTop;
        const top = sig!.offsetTop;
        const left = sig!.offsetLeft;
        const xPos = Math.floor(((left - boundX - 9) / dpi) * 72);

        const h = Math.floor((sig!.offsetHeight / dpi) * 72);
        const yPos = 791 - Math.ceil(((top - boundY - 10) / dpi) * 72) - h;

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
    const request = {
      tokenInfo: { serial: '540110000b4525650231e39369660895', pin: '079073009568' },
      signingRequestContents: [
        {
          data: this.pdfSrc.toString().replace('data:application/pdf;base64,', ''),
          location: { visibleX: $('#xPos').text(), visibleY: $('#yPos').text() },
          extraInfo: { pageNum: Number(this.renderTextMode) },
        },
      ],
    };

    this.signingService.signPdf(request).subscribe(response => {
      const byteArray = this.base64ToArrayBuffer(response);
      saveAs(new Blob([byteArray], { type: 'application/pdf' }), Date.now().toString());
      this.signEvent.emit(true);
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
