import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import * as $ from 'jquery';
import swal from 'sweetalert';
import { SigningService } from 'app/core/signing/signing.service';
import { saveAs } from 'file-saver';
import Swal from 'sweetalert2';
import { FormBuilder, Validators } from '@angular/forms';
@Component({
  selector: 'jhi-pdf-view',
  templateUrl: './pdf-view.component.html',
  styleUrls: ['./pdf-view.component.scss'],
})
export class PdfViewComponent implements OnInit, AfterViewInit {
  @ViewChild('viewer') viewer: ElementRef | undefined;
  base64Content: any;
  content: any;
  scale: any = 1.25;
  rectW: any = 265;
  rectH: any = 65;
  rectmoveW: any;
  rectmoveH: any;
  currentPage: any;

  certificateInfoForm = this.fb.group({
    serial: ['', [Validators.required]],
    pin: ['', [Validators.required]],
  });

  public isCheckShow: any;

  constructor(private signingService: SigningService, private fb: FormBuilder) {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    // $('#viewer').on('click', '.page', this.signWithServer.bind(this));
    // $('#viewerContainer').on('mousemove', '.page', this.onMouseMove.bind(this));
    $("#scaleSelect option[value='page-fit']").remove();
    $("#scaleSelect option[value='page-actual']").remove();
    $("#scaleSelect option[value='page-width']").remove();
  }

  fileChangeEvent(event: any): void {
    const selectedFile = event.target.files;
    this.content = selectedFile[0];

    const file = event.target.files[0];
    const reader = new FileReader();
    reader.readAsDataURL(file);

    reader.onload = () => {
      this.base64Content = reader.result!.toString().replace('data:application/pdf;base64,', '');
    };
  }

  // onZoomChange(event: any): void {
  //   const rectmove = document.getElementById('rectMove');
  //   if (event === 'auto') {
  //     this.scale = 1.25;
  //   } else this.scale = event / 100;
  //
  //   rectmove!.style.width = this.rectW * this.scale + 'px';
  //   rectmove!.style.height = this.rectH * this.scale + 'px';
  // }

  // signWithServer(e: any): any {
  //   Swal.fire({
  //     // title: 'Thông báo',
  //     title: 'Mời bạn nhập thông tin chứng thư số',
  //     icon: 'warning',
  //     showCancelButton: true,
  //     allowEnterKey: true,
  //     html: `<form [formGroup]="certificateInfoForm">
  //               <input type="text" id="serial" class="swal2-input" placeholder="Serial" formControlName="serial" (change)="test()">
  //
  //               <div *ngIf="certificateInfoForm.get('serial')!.invalid && (certificateInfoForm.get('serial')!.dirty || certificateInfoForm.get('pin')!.touched)">
  //                       <small
  //                           class="form-text text-danger"
  //                           *ngIf="certificateInfoForm.get('serial')?.error?.required"
  //                        >
  //                               This field is required.
  //                       </small>
  //               </div>
  //               <input type="password" id="pin" class="swal2-input" placeholder="Pin" formControlName="pin">
  //           </form> `,
  //     confirmButtonText: 'Ký',
  //     cancelButtonText: 'Thử lại',
  //   }).then(result => {
  //     if (result.value) {
  //       const offsetX = e.currentTarget.getBoundingClientRect().x;
  //       const offsetY = e.currentTarget.getBoundingClientRect().y;
  //       const positionX = e.pageX - offsetX;
  //       const positionY = e.pageY - offsetY;
  //       const xDifference = -5;
  //       const yDifference = 60;
  //       const dpi = 96;
  //       const pdfPositionX = Math.round((((positionX + 1) / this.scale) * 72) / dpi) + xDifference;
  //       const pdfPositionY = Math.round((((e.currentTarget.clientHeight! - positionY + yDifference - 2) / this.scale) * 72) / dpi);
  //       const pageNumber = this.currentPage;
  //
  //       const request = {
  //         tokenInfo: { serial: '540110000b4525650231e39369660895', pin: '079073009568' },
  //         signingRequestContents: [
  //           { data: this.base64Content, location: { visibleX: pdfPositionX, visibleY: pdfPositionY }, extraInfo: { pageNum: pageNumber } },
  //         ],
  //       };
  //       this.signingService.signPdf(request).subscribe(response => {
  //         const byteArray = this.base64ToArrayBuffer(response);
  //         this.content = byteArray;
  //         saveAs(new Blob([byteArray], { type: 'application/pdf' }), 'file_signed.pdf');
  //         Swal.fire(
  //           // 'Thông báo',
  //           // 'Tệp của bạn được ký thành công!',
  //           // 'success',
  //           {
  //             title: 'Thông báo',
  //             text: 'Tệp của bạn được ký thành công!',
  //             icon: 'success',
  //             showCancelButton: false,
  //             showConfirmButton: true,
  //             // confirmButtonText: 'Đồng ý',
  //             confirmButtonText: 'Xem tệp',
  //           }
  //         );
  //       });
  //       // For more information about handling dismissals please visit
  //       // https://sweetalert2.github.io/#handling-dismissals
  //     } //else if (result.dismiss === Swal.DismissReason.cancel) {
  //     //Swal.fire(
  //     //'Cancelled',
  //     //'Your imaginary file is safe :)',
  //     //'error'
  //     //)
  //     //}
  //   });
  // }

  // public onMouseMove(e: any): void {
  //   const x = e.pageX + 2;
  //   const y = e.pageY;
  //
  //   this.getInfoViewer();
  //
  //   const checkShowSidebar = $('#sidebarToggle').get(0).getAttribute('class') === 'toolbarButton toggled';
  //
  //   const offsetRight = checkShowSidebar
  //     ? document.getElementById('accordionSidebar')!.offsetWidth +
  //       document.getElementById('sidebarContent')!.offsetWidth +
  //       $('.page').get(0).offsetLeft +
  //       $('.page').get(0).offsetWidth
  //     : document.getElementById('accordionSidebar')!.offsetWidth + $('.page').get(0).offsetLeft + $('.page').get(0).offsetWidth;
  //
  //   const page = document.querySelector<HTMLElement>('.page');
  //   const offset = $('.page').offset();
  //   const offsetY = offset!.top + page!.clientTop;
  //
  //   if (e.pageX + this.rectW * this.scale > offsetRight + 20 || e.pageY * this.currentPage - offsetY < this.rectH * this.scale) {
  //     $('#rectMove').hide();
  //     return;
  //   }
  //
  //   this.isCheckShow = true;
  //   $('#isShowRect').val(1);
  //   $('#rectMove').show();
  //   this.handleMouseMove(e);
  // }

  // getInfoViewer(): void {
  //   const clientW = 180;
  //   const clientH = 80;
  //   const baseH = 72;
  //   const dpi = 96;
  //
  //   this.rectmoveW = 265 * this.scale;
  //   this.rectmoveH = 65 * this.scale;
  //   this.currentPage = $('#pageNumber').val();
  // }

  // handleMouseMove(e: any): void {
  //   const rectmove = document.getElementById('rectMove');
  //   const x = e.pageX + 2;
  //   const y = e.pageY - rectmove!.clientHeight + 170 - $('#viewerContainer').offset()!.top;
  //   $('#rectMove').animate(
  //     {
  //       left: x,
  //       top: y,
  //     },
  //     0
  //   );
  // }

  // base64ToArrayBuffer(base64: any): ArrayBuffer {
  //   const binaryString = window.atob(base64);
  //   const len = binaryString.length;
  //   const bytes = new Uint8Array(len);
  //   for (let i = 0; i < len; i++) {
  //     bytes[i] = binaryString.charCodeAt(i);
  //   }
  //   return bytes.buffer;
  // }
}
