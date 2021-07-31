import { Component, Input, OnInit } from '@angular/core';
import { SignatureTemplateService } from 'app/entities/signature-template/signature-template.service';
import { ISignatureTemplate } from 'app/shared/model/signature-template.model';
import { HttpHeaders } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-signature-list',
  templateUrl: './signature-list.component.html',
  styleUrls: ['./signature-list.component.scss'],
})
export class SignatureListComponent implements OnInit {
  @Input() userId: any;
  templates: ISignatureTemplate[] | null | undefined;
  totalItems = 0;
  itemsPerPage = 5;
  page = 0;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(protected signatureTemplateService: SignatureTemplateService, public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
    const pageToLoad = 1;
    const dataRequest = {
      page: this.page,
      size: this.itemsPerPage,
      sort: this.sort(),
      userId: this.userId,
    };
    this.signatureTemplateService
      .getSignatureTemplateByUserID(dataRequest)
      .subscribe((res: any) => this.onSuccess(res.body, res.headers, pageToLoad));
  }

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page || 1;

    const dataRequest = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      userId: this.userId,
    };
    this.signatureTemplateService
      .getSignatureTemplateByUserID(dataRequest)
      .subscribe((res: any) => this.onSuccess(res.body, res.headers, pageToLoad));
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  protected onSuccess(data: ISignatureTemplate[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;

    this.templates = data || [];
    this.ngbPaginationPage = this.page;
  }

  convertToSafeImgContent(base64Content: any): string {
    return 'data:image/png;base64,' + base64Content;
  }
  chooseSignatureTemplate(templateId: number): void {
    this.activeModal.close(templateId);
  }
}
