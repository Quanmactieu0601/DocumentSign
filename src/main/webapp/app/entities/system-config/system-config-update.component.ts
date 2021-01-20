import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { KEYS } from 'app/shared/constants/system-config.constants';
import { DATATYPES } from 'app/shared/constants/system-config.constants';
import { ISystemConfig, SystemConfig } from 'app/shared/model/system-config.model';
import { SystemConfigService } from './system-config.service';

@Component({
  selector: 'jhi-system-config-update',
  templateUrl: './system-config-update.component.html',
})
export class SystemConfigUpdateComponent implements OnInit {
  systemConfig!: SystemConfig;
  isSaving = false;
  keys = KEYS;
  dataTypes = DATATYPES;
  isCheck = false;
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
      if (systemConfig) {
        this.systemConfig = systemConfig;
      }
      this.updateForm(this.systemConfig);
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
    if (systemConfig.dataType=='BOOLEAN'){
      this.isCheck = systemConfig.value != '0';
    }
  }



  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.createFromForm(this.systemConfig);
    if (this.systemConfig.id !== undefined) {
      this.subscribeToSaveResponse(this.systemConfigService.update(this.systemConfig));
    } else {
      this.subscribeToSaveResponse(this.systemConfigService.create(this.systemConfig));
    }
  }

  private createFromForm(systemConfig: SystemConfig): void {
    systemConfig.id = this.editForm.get(['id'])!.value;
    systemConfig.comId = this.editForm.get(['comId'])!.value;
    systemConfig.key = this.editForm.get(['key'])!.value;
    if(this.editForm.get(['value'])!.value.toString() === 'true'){
      systemConfig.value = '1';
    } else if(this.editForm.get(['value'])!.value.toString() === 'false'){
      systemConfig.value = '0';
    } else systemConfig = this.editForm.get(['value'])!.value.toString();
    systemConfig.description = this.editForm.get(['description'])!.value;
    systemConfig.dataType = this.editForm.get(['dataType'])!.value;
    systemConfig.activated = this.editForm.get(['activated'])!.value;
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
