import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISystemConfigCategory } from 'app/shared/model/system-config-category.model';

@Component({
  selector: 'jhi-system-config-category-detail',
  templateUrl: './system-config-category-detail.component.html',
})
export class SystemConfigCategoryDetailComponent implements OnInit {
  systemConfigCategory: ISystemConfigCategory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemConfigCategory }) => (this.systemConfigCategory = systemConfigCategory));
  }

  previousState(): void {
    window.history.back();
  }
}
