import { AfterViewInit, Component, ElementRef, HostListener, OnInit, Renderer2, ViewChild } from '@angular/core';
import { SigningService } from 'app/core/signing/signing.service';
import { FormBuilder, Validators } from '@angular/forms';
import * as $ from 'jquery';
import 'jquery-ui/ui/widgets/draggable.js';
import { PdfViewerComponent } from 'ng2-pdf-viewer';

@Component({
  selector: 'jhi-pdf-view',
  templateUrl: './pdf-view.component.html',
  styleUrls: ['./pdf-view.component.scss'],
})
export class PdfViewComponent implements OnInit {
  @ViewChild(PdfViewerComponent) private pdfComponent: PdfViewerComponent | undefined;

  title = 'angular-pdf-viewer-app';
  pdfSrc = 'https://vadimdez.github.io/ng2-pdf-viewer/assets/pdf-test.pdf';

  renderText = true;
  originalSize = false;
  fitToPage = false;
  showAll = true;
  autoresize = false;
  showBorders = true;
  renderTextModes = [1, 2, 3];
  renderTextMode = 1;
  rotation = 0;
  zoom = 1;
  zoomScale = 'page-width';
  zoomScales = ['page-width', 'page-fit', 'page-height'];
  pdfQuery = '';
  totalPages!: number;

  certificateInfoForm = this.fb.group({
    serial: ['', [Validators.required]],
    pin: ['', [Validators.required]],
  });

  public isCheckShow: any;

  constructor(
    private signingService: SigningService,
    private fb: FormBuilder,
    private elementRef: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {}

  zoomIn(): void {
    this.zoom += 0.05;
  }

  zoomOut(): void {
    if (this.zoom > 0.05) this.zoom -= 0.05;
  }

  rotateDoc(): void {
    this.rotation += 90;
  }

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

  // Event handler when new PDF file is selected
  onFileSelected(): void {
    const $pdf: any = document.querySelector('#file');

    if (typeof FileReader !== 'undefined') {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        this.pdfSrc = e.target.result;
      };

      reader.readAsArrayBuffer($pdf.files[0]);
    }
  }

  callBackFn(event: any): void {
    console.warn('callBackFn', event);
    // Setting total number of pages
    this.totalPages = event._pdfInfo.numPages;
    // const element = this.renderer.selectRootElement('canvas', true);
    const element = document.getElementsByClassName('pdfViewer')[0];
    // const text = this.renderer.createText('Namaste!!!!!');
    const child = document.createElement('div');
    child.setAttribute('id', 'signature-box');
    (child as HTMLElement).style.backgroundColor = 'red';
    (child as HTMLElement).style.position = 'absolute';
    (child as HTMLElement).style.top = '0px';
    (child as HTMLElement).style.zIndex = '9';
    (child as HTMLElement).style.width = '256px';
    (child as HTMLElement).style.height = '65px';
    // // this.renderer.setAttribute(child, 'ngDraggable','');
    this.renderer.appendChild(element, child);
  }
  pageRendered(event: any): void {
    console.warn('pageRendered', event);

    const element = document.getElementsByClassName('page')[0];

    ($('#signature-box') as any).draggable({
      containment: element,
    });
  }

  setSignatureInPage(numberPage: any): void {
    const element = document.getElementsByClassName('page')[numberPage.target.value - 1];

    ($('#signature-box') as any).draggable({
      containment: element,
    });

    $('#signature-box').animate(
      {
        left: 300,
        top: numberPage.target.value * 1100,
      },
      0
    );
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
