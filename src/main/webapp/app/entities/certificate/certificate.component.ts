import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICertificate } from 'app/shared/model/certificate.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { CertificateService } from './certificate.service';
import { CertificateDeleteDialogComponent } from './certificate-delete-dialog.component';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'jhi-certificate',
  templateUrl: './certificate.component.html',
})
export class CertificateComponent implements OnInit, OnDestroy {
  certificates?: ICertificate[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  certificateSearch = this.fb.group({
    alias: [],
    ownerId: [],
    serial: [],
    validDate: [],
    expiredDate: [],
  });
  constructor(
    protected certificateService: CertificateService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected fb: FormBuilder
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;

    this.certificateService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<ICertificate[]>) => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
        () => this.onError()
      );
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInCertificates();
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.searchCertificate();
      }
    }).subscribe();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICertificate): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCertificates(): void {
    this.eventSubscriber = this.eventManager.subscribe('certificateListModification', () => this.loadPage());
  }

  delete(certificate: ICertificate): void {
    const modalRef = this.modalService.open(CertificateDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.certificate = certificate;
  }

  updateStatus(id: any): void {
    this.certificateService.updateActiveStatus(id).subscribe((res: any) => {
      if (res.ok) {
        this.searchCertificate();
      }
    });
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: ICertificate[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/certificate'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.certificates = data || [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  searchCertificate(): any {
    const data = {
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      ...this.certificateSearch.value,
    };

    data.alias = data.alias ? data.alias.trim() : null;
    data.ownerId = data.ownerId ? data.ownerId.trim() : null;
    data.serial = data.serial ? data.serial.trim() : null;
    data.validDate = data.validDate ? data.validDate.trim() : null;
    data.expiredDate = data.expiredDate ? data.expiredDate.trim() : null;

    this.certificateService.findCertificate(data).subscribe((res: any) => this.onSuccess(res.body, res.headers, this.page, false));
  }
}
