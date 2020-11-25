import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ITransaction } from 'app/shared/model/transaction.model';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { TransactionService } from './transaction.service';
import { TransactionDeleteDialogComponent } from './transaction-delete-dialog.component';
import { FormBuilder } from '@angular/forms';
import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'jhi-transaction',
  templateUrl: './transaction.component.html',
})
export class TransactionComponent implements OnInit, OnDestroy {
  transactions: ITransaction[] | null = null;
  eventSubscriber?: Subscription;
  searchForm = this.fb.group({
    // id: [],
    api: [],
    code: [],
    message: [],
    data: [],
    type: [],
    host: [],
    method: [],
    fullName: [],
    triggerTime: [],
    startDate: [],
    endDate: [],
  });
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected transactionService: TransactionService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected fb: FormBuilder
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;
    // this.transactionService
    //   .query({
    //     page: pageToLoad - 1,
    //     size: this.itemsPerPage,
    //     sort: this.sort(),
    //   })
    //   .subscribe(
    //     (res: HttpResponse<ITransaction[]>) => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
    //     () => this.onError()
    //   );
    this.searchTransactions(page);
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInTransactions();
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
        this.searchTransactions();
      }
    }).subscribe();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITransaction): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }
  registerChangeInTransactions(): void {
    this.eventSubscriber = this.eventManager.subscribe('transactionListModification', () => this.loadPage());
  }
  searchTransactions(page?: number): any {
    const pageToLoad: number = page || this.page || 1;
    const data1 = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      ...this.searchForm.value,
    };
    if (data1.api != null) {
      data1.api = data1.api.trim();
    }
    if (data1.code != null) {
      data1.code = data1.code.trim();
    }
    if (data1.message != null) {
      data1.message = data1.message.trim();
    }
    if (data1.data != null) {
      data1.data = data1.data.trim();
    }
    if (data1.type != null) {
      data1.type = data1.type.trim();
    }
    if (data1.host != null) {
      data1.host = data1.host.trim();
    }
    if (data1.method != null) {
      data1.method = data1.method.trim();
    }
    if (data1.fullName != null) {
      data1.fullName = data1.fullName.trim();
    }
    if (data1.triggerTime != null) {
      data1.triggerTime = data1.triggerTime.trim();
    }
    if (data1.startDate != null) {
      data1.startDate = data1.startDate.trim();
    }
    if (data1.endDate != null) {
      data1.endDate = data1.endDate.trim();
    }
    this.transactionService
      .findByTransaction(data1)
      .subscribe((res: HttpResponse<any>) => this.onSuccess(res.body, res.headers, pageToLoad, false));
  }

  delete(transaction: ITransaction): void {
    const modalRef = this.modalService.open(TransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.transaction = transaction;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: ITransaction[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/transaction'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.transactions = data || [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
