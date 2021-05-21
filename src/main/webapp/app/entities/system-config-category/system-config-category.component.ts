import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISystemConfigCategory } from 'app/shared/model/system-config-category.model';
import { SystemConfigCategoryService } from './system-config-category.service';
import { SystemConfigCategoryDeleteDialogComponent } from './system-config-category-delete-dialog.component';

@Component({
  selector: 'jhi-system-config-category',
  templateUrl: './system-config-category.component.html',
})
export class SystemConfigCategoryComponent implements OnInit, OnDestroy {
  systemConfigCategories?: ISystemConfigCategory[];
  eventSubscriber?: Subscription;

  constructor(
    protected systemConfigCategoryService: SystemConfigCategoryService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.systemConfigCategoryService
      .query()
      .subscribe((res: HttpResponse<ISystemConfigCategory[]>) => (this.systemConfigCategories = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInSystemConfigCategories();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ISystemConfigCategory): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInSystemConfigCategories(): void {
    this.eventSubscriber = this.eventManager.subscribe('systemConfigCategoryListModification', () => this.loadAll());
  }

  delete(systemConfigCategory: ISystemConfigCategory): void {
    const modalRef = this.modalService.open(SystemConfigCategoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.systemConfigCategory = systemConfigCategory;
  }
}
