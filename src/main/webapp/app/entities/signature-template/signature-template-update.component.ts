import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISignatureTemplate, SignatureTemplate } from 'app/shared/model/signature-template.model';
import { SignatureTemplateService } from './signature-template.service';
import { UserService } from 'app/core/user/user.service';
import { IUser } from 'app/core/user/user.model';
import { ICoreParser } from 'app/shared/model/core-parser.model';
import { CoreParserService } from 'app/entities/core-parser/core-parser.service';

@Component({
  selector: 'jhi-signature-template-update',
  templateUrl: './signature-template-update.component.html',
})
export class SignatureTemplateUpdateComponent implements OnInit {
  isSaving = false;
  users?: IUser[] | null;
  coreParsers?: ICoreParser[] | null;
  editForm = this.fb.group({
    id: [],
    // signatureImage: [],
    createdDate: [],
    createdBy: [],
    coreParser: [],
    userId: [],
    htmlTemplate: [],
  });

  constructor(
    protected signatureTemplateService: SignatureTemplateService,
    protected activatedRoute: ActivatedRoute,
    private userService: UserService,
    private CoreParserService: CoreParserService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signatureTemplate }) => {
      this.updateForm(signatureTemplate);
      this.userService.getAllUsers().subscribe((res: HttpResponse<IUser[]>) => {
        this.users = res.body;
      });

      this.CoreParserService.findAll().subscribe((res: HttpResponse<ICoreParser[]>) => {
        this.coreParsers = res.body;
      });
    });
  }

  updateForm(signatureTemplate: ISignatureTemplate): void {
    this.editForm.patchValue({
      id: signatureTemplate.id,
      userId: signatureTemplate.userId,
      createdDate: signatureTemplate.createdDate,
      createdBy: signatureTemplate.createdBy,
      coreParser: signatureTemplate.coreParser,
      htmlTemplate: signatureTemplate.htmlTemplate,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const signatureTemplate = this.createFromForm();
    if (signatureTemplate.id !== undefined) {
      this.subscribeToSaveResponse(this.signatureTemplateService.update(signatureTemplate));
    } else {
      this.subscribeToSaveResponse(this.signatureTemplateService.create(signatureTemplate));
    }
  }

  private createFromForm(): ISignatureTemplate {
    return {
      ...new SignatureTemplate(),
      id: this.editForm.get(['id'])!.value,
      // signatureImage: this.editForm.get(['signatureImage'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISignatureTemplate>>): void {
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
