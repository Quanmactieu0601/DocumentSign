import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import * as $ from 'jquery';
import { doc } from 'prettier';
@Component({
  selector: 'jhi-pdf-view',
  templateUrl: './pdf-view.component.html',
  styleUrls: ['./pdf-view.component.scss'],
})
export class PdfViewComponent implements OnInit, AfterViewInit {
  @ViewChild('viewer') viewer: ElementRef | undefined;
  base64: any;
  content: any;
  scale: any = 1.25;
  rectW: any = 265;
  rectH: any = 65;
  rectmoveW: any;
  rectmoveH: any;
  currentPage: any;
  isCheckShow: any;

  constructor() {}

  ngOnInit(): void {
    $('#viewerContainer').on('click', function (): void {
      if ($('#isShowRect').val() === '1') {
        alert('bạn muốn ký');
      }
    });
    // $('#viewerContainer').on("mouseleave", function () : void {
    //   // $("#rectMove").hide();
    //   $('#viewerContainer').css("background-color", "lightgray");
    // });
    //
    // $('#viewerContainer').on("mouseenter", function () : void {
    //   // $("#rectMove").show();
    //   $('#viewerContainer').css("background-color", "green");
    // });
  }

  ngAfterViewInit(): void {
    const test = 'ok';
  }

  fileChangeEvent(event: any): void {
    const selectedFile = event.target.files;
    this.content = selectedFile[0];
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(e: any): void {
    const x = e.pageX + 2;
    const y = e.pageY;

    // console.warn(`x = ${x}` + `y = ${y}`)
    this.getInfoViewer();

    const rectmove = document.getElementById('rectMove');
    rectmove!.style.width = this.rectmoveW + 'px';
    rectmove!.style.height = this.rectmoveH + 'px';

    const page = document.querySelector<HTMLElement>('.page');
    let ofl;
    try {
      ofl = page!.offsetLeft;
    } catch (exception) {
      ofl = 0;
    }
    const comment = document.getElementById('comment-wrapper');
    const offsetX = ofl + page!.clientLeft + $('#mainContainer').offset()!.left;
    const offset = $('.page').offset();
    const offsetY = offset!.top + page!.clientTop;
    if (
      e.pageX - offsetX < 0 ||
      e.pageX - offsetX + this.rectW * this.scale > page!.clientWidth ||
      e.pageY * this.currentPage - offsetY < this.rectH * this.scale
    ) {
      $('#isShowRect').val(0);
      $('#rectMove').hide();
      this.isCheckShow = false;
      return;
    }
    this.isCheckShow = true;
    $('#isShowRect').val(1);
    $('#rectMove').show();
    this.handleMouseMove(e);
  }

  getInfoViewer(): void {
    this.scale = Number($('#scaleSelect').val()) ? Number($('#scaleSelect').val()) : 1.25;

    const clientW = 180;
    const clientH = 80;
    const baseH = 72;
    const dpi = 96;

    this.rectW = (clientW * dpi) / baseH;
    this.rectH = (clientH * dpi) / baseH;

    this.rectmoveW = 265 * this.scale;
    this.rectmoveH = 65 * this.scale;
    this.currentPage = $('#pageNumber').val();
  }

  handleMouseMove(e: any): void {
    const rectmove = document.getElementById('rectMove');
    const x = e.pageX;
    const y = e.pageY - rectmove!.clientHeight + 170 - $('#viewerContainer').offset()!.top;
    $('#rectMove').animate(
      {
        left: x,
        top: y,
      },
      0
    );
  }
}
