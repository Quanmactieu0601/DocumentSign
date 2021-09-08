import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ICertPackage, CertPackage } from 'app/shared/model/cert-package.model';
import { CertPackageService } from './cert-package.service';

@Component({
  selector: 'jhi-cert-package-update',
  templateUrl: './cert-package-update.component.html',
})
export class CertPackageUpdateComponent implements OnInit {
  isSaving = false;
  expiredDateDp: any;

  editForm = this.fb.group({
    id: [],
    packageCode: [],
    certType: [],
    nameCert: [],
    keyLength: [],
    expiredDate: [],
    signingTurn: [],
    price: [],
  });

  constructor(protected certPackageService: CertPackageService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certPackage }) => {
      this.updateForm(certPackage);
    });
  }

  updateForm(certPackage: ICertPackage): void {
    this.editForm.patchValue({
      id: certPackage.id,
      packageCode: certPackage.packageCode,
      certType: certPackage.certType,
      nameCert: certPackage.nameCert,
      keyLength: certPackage.keyLength,
      expiredDate: certPackage.expiredDate,
      signingTurn: certPackage.signingTurn,
      price: certPackage.price,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const certPackage = this.createFromForm();
    if (certPackage.id !== undefined) {
      this.subscribeToSaveResponse(this.certPackageService.update(certPackage));
    } else {
      this.subscribeToSaveResponse(this.certPackageService.create(certPackage));
    }
  }

  private createFromForm(): ICertPackage {
    return {
      ...new CertPackage(),
      id: this.editForm.get(['id'])!.value,
      packageCode: this.editForm.get(['packageCode'])!.value,
      certType: this.editForm.get(['certType'])!.value,
      nameCert: this.editForm.get(['nameCert'])!.value,
      keyLength: this.editForm.get(['keyLength'])!.value,
      expiredDate: this.editForm.get(['expiredDate'])!.value,
      signingTurn: this.editForm.get(['signingTurn'])!.value,
      price: this.editForm.get(['price'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICertPackage>>): void {
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
