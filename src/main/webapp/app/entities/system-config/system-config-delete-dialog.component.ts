import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISystemConfig } from 'app/shared/model/system-config.model';
import { SystemConfigService } from './system-config.service';

@Component({
  templateUrl: './system-config-delete-dialog.component.html',
})
export class SystemConfigDeleteDialogComponent {
  systemConfig?: ISystemConfig;

  constructor(
    protected systemConfigService: SystemConfigService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.systemConfigService.delete(id).subscribe(() => {
      this.eventManager.broadcast('systemConfigListModification');
      this.activeModal.close();
    });
  }
}
