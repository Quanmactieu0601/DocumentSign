import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISystemConfigCategory, SystemConfigCategory } from 'app/shared/model/system-config-category.model';
import { SystemConfigCategoryService } from './system-config-category.service';

@Component({
  selector: 'jhi-system-config-category-update',
  templateUrl: './system-config-category-update.component.html',
})
export class SystemConfigCategoryUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    configKey: [],
    dataType: [],
    description: [],
  });

  constructor(
    protected systemConfigCategoryService: SystemConfigCategoryService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemConfigCategory }) => {
      this.updateForm(systemConfigCategory);
    });
  }

  updateForm(systemConfigCategory: ISystemConfigCategory): void {
    this.editForm.patchValue({
      id: systemConfigCategory.id,
      configKey: systemConfigCategory.configKey,
      dataType: systemConfigCategory.dataType,
      description: systemConfigCategory.description,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const systemConfigCategory = this.createFromForm();
    if (systemConfigCategory.id !== undefined) {
      this.subscribeToSaveResponse(this.systemConfigCategoryService.update(systemConfigCategory));
    } else {
      this.subscribeToSaveResponse(this.systemConfigCategoryService.create(systemConfigCategory));
    }
  }

  private createFromForm(): ISystemConfigCategory {
    return {
      ...new SystemConfigCategory(),
      id: this.editForm.get(['id'])!.value,
      configKey: this.editForm.get(['configKey'])!.value,
      dataType: this.editForm.get(['dataType'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISystemConfigCategory>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
