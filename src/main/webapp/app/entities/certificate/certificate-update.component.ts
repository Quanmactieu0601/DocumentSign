import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ICertificate, Certificate } from 'app/shared/model/certificate.model';
import { CertificateService } from './certificate.service';

@Component({
  selector: 'jhi-certificate-update',
  templateUrl: './certificate-update.component.html',
})
export class CertificateUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    lastUpdate: [],
    tokenType: [],
    serial: [],
    ownerTaxcode: [],
    subjectInfo: [],
    alias: [],
    tokenInfoDTO: [],
    rawData: [],
  });

  constructor(protected certificateService: CertificateService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certificate }) => {
      this.updateForm(certificate);
    });
  }

  updateForm(certificate: ICertificate): void {
    this.editForm.patchValue({
      id: certificate.id,
      lastUpdate: certificate.lastUpdate,
      tokenType: certificate.tokenType,
      serial: certificate.serial,
      ownerTaxcode: certificate.ownerTaxcode,
      subjectInfo: certificate.subjectInfo,
      alias: certificate.alias,
      tokenInfo: certificate.tokenInfo,
      rawData: certificate.rawData,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const certificate = this.createFromForm();
    if (certificate.id !== undefined) {
      this.subscribeToSaveResponse(this.certificateService.update(certificate));
    } else {
      this.subscribeToSaveResponse(this.certificateService.create(certificate));
    }
  }

  private createFromForm(): ICertificate {
    return {
      ...new Certificate(),
      id: this.editForm.get(['id'])!.value,
      lastUpdate: this.editForm.get(['lastUpdate'])!.value,
      tokenType: this.editForm.get(['tokenType'])!.value,
      serial: this.editForm.get(['serial'])!.value,
      ownerTaxcode: this.editForm.get(['ownerTaxcode'])!.value,
      subjectInfo: this.editForm.get(['subjectInfo'])!.value,
      alias: this.editForm.get(['alias'])!.value,
      tokenInfo: this.editForm.get(['tokenInfo'])!.value,
      rawData: this.editForm.get(['rawData'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICertificate>>): void {
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
