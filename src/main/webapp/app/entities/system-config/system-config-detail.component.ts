import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISystemConfig } from 'app/shared/model/system-config.model';

@Component({
  selector: 'jhi-system-config-detail',
  templateUrl: './system-config-detail.component.html',
})
export class SystemConfigDetailComponent implements OnInit {
  systemConfig: ISystemConfig | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemConfig }) => (this.systemConfig = systemConfig));
  }

  previousState(): void {
    window.history.back();
  }
}
