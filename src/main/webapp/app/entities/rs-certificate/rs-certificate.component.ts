import { Component, OnInit } from '@angular/core';
import { IRsCertificate } from 'app/shared/model/rs-Certificate.model';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { FormBuilder } from '@angular/forms';
import { combineLatest, Subscription } from 'rxjs';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { ICertificate } from 'app/shared/model/certificate.model';
import { HttpHeaders } from '@angular/common/http';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  selector: 'jhi-rs-certificate',
  templateUrl: './rs-certificate.component.html',
  styleUrls: ['./rs-certificate.component.scss'],
})
export class RsCertificateComponent implements OnInit {
  rsCertificates?: IRsCertificate[];
  eventSubscriber?: Subscription;
  ngbPaginationPage = 1;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;

  rsCertificateSearch = this.fb.group({
    serial: [],
    validDate: [],
    expiredDate: [],
  });

  constructor(
    private certificateService: CertificateService,
    protected fb: FormBuilder,
    protected activatedRoute: ActivatedRoute,
    protected eventManager: JhiEventManager,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInCertificates();
  }

  handleNavigation(): void {
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

  registerChangeInCertificates(): void {
    this.eventSubscriber = this.eventManager.subscribe('certificateListModification', () => this.loadPage());
  }

  trackId(index: number, item: IRsCertificate): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  loadPage(page?: number): void {
    this.searchCertificate(page);
  }

  searchCertificate(page?: number): any {
    const pageToLoad: number = page || this.page || 1;
    const data = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      type: 1,
      sort: this.sort(),
      ...this.rsCertificateSearch.value,
    };
    data.serial = data.serial ? data.serial.trim() : null;
    data.validDate = data.validDate ? data.validDate.trim() : null;
    data.expiredDate = data.expiredDate ? data.expiredDate.trim() : null;

    this.certificateService.findCertificate(data).subscribe((res: any) => this.onSuccess(res.body, res.headers, pageToLoad, false));
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
      this.router.navigate(['/rs-certificate'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.rsCertificates = data || [];
    this.ngbPaginationPage = this.page;
  }
}
