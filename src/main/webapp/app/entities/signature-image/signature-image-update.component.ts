import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISignatureImage, SignatureImage } from 'app/shared/model/signature-image.model';
import { SignatureImageService } from './signature-image.service';

@Component({
  selector: 'jhi-signature-image-update',
  templateUrl: './signature-image-update.component.html',
})
export class SignatureImageUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    imgData: [],
    userId: [],
  });

  constructor(protected signatureImageService: SignatureImageService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signatureImage }) => {
      this.updateForm(signatureImage);
    });
  }

  updateForm(signatureImage: ISignatureImage): void {
    this.editForm.patchValue({
      id: signatureImage.id,
      imgData: signatureImage.imgData,
      userId: signatureImage.userId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const signatureImage = this.createFromForm();
    if (signatureImage.id !== undefined) {
      this.subscribeToSaveResponse(this.signatureImageService.update(signatureImage));
    } else {
      this.subscribeToSaveResponse(this.signatureImageService.create(signatureImage));
    }
  }

  private createFromForm(): ISignatureImage {
    return {
      ...new SignatureImage(),
      id: this.editForm.get(['id'])!.value,
      imgData: this.editForm.get(['imgData'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISignatureImage>>): void {
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
