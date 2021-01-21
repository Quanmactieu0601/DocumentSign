import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISystemConfigCategory } from 'app/shared/model/system-config-category.model';
import { SystemConfigCategoryService } from './system-config-category.service';

@Component({
  templateUrl: './system-config-category-delete-dialog.component.html',
})
export class SystemConfigCategoryDeleteDialogComponent {
  systemConfigCategory?: ISystemConfigCategory;

  constructor(
    protected systemConfigCategoryService: SystemConfigCategoryService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.systemConfigCategoryService.delete(id).subscribe(() => {
      this.eventManager.broadcast('systemConfigCategoryListModification');
      this.activeModal.close();
    });
  }
}
