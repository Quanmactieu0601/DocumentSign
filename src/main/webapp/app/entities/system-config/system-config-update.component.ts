import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISystemConfig, SystemConfig } from 'app/shared/model/system-config.model';
import { SystemConfigService } from './system-config.service';

@Component({
  selector: 'jhi-system-config-update',
  templateUrl: './system-config-update.component.html',
})
export class SystemConfigUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    comId: [],
    key: [],
    value: [],
    description: [],
    dataType: [],
    activated: [],
  });

  constructor(protected systemConfigService: SystemConfigService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemConfig }) => {
      this.updateForm(systemConfig);
    });
  }

  updateForm(systemConfig: ISystemConfig): void {
    this.editForm.patchValue({
      id: systemConfig.id,
      comId: systemConfig.comId,
      key: systemConfig.key,
      value: systemConfig.value,
      description: systemConfig.description,
      dataType: systemConfig.dataType,
      activated: systemConfig.activated,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const systemConfig = this.createFromForm();
    if (systemConfig.id !== undefined) {
      this.subscribeToSaveResponse(this.systemConfigService.update(systemConfig));
    } else {
      this.subscribeToSaveResponse(this.systemConfigService.create(systemConfig));
    }
  }

  private createFromForm(): ISystemConfig {
    return {
      ...new SystemConfig(),
      id: this.editForm.get(['id'])!.value,
      comId: this.editForm.get(['comId'])!.value,
      key: this.editForm.get(['key'])!.value,
      value: this.editForm.get(['value'])!.value,
      description: this.editForm.get(['description'])!.value,
      dataType: this.editForm.get(['dataType'])!.value,
      activated: this.editForm.get(['activated'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISystemConfig>>): void {
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
