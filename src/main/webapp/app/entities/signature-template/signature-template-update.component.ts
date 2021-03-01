import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
import { TranslateService } from '@ngx-translate/core';
import { Authority } from 'app/shared/constants/authority.constants';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-signature-template-update',
  templateUrl: './signature-template-update.component.html',
})
export class SignatureTemplateUpdateComponent implements OnInit {
  @ViewChild('width') width: ElementRef | undefined;
  @ViewChild('height') height: ElementRef | undefined;
  @ViewChild('signatureImage') signatureImage: ElementRef | undefined;
  @ViewChild('userDropdown') userDropdown: ElementRef | undefined;
  @ViewChild('coreParserDropdown') coreParserDropdown: ElementRef | undefined;

  signatureTemplate!: SignatureTemplate;
  isSaving = false;
  users?: IUser[] | null;
  coreParsers?: ICoreParser[] | null;
  signatureImageExam?: String | null;
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
    private coreParserService: CoreParserService,
    protected accountService: AccountService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signatureTemplate }) => {
      if (signatureTemplate) {
        this.signatureTemplate = signatureTemplate;
      }
      this.updateForm(signatureTemplate);
      this.userService.getAllUsers().subscribe((res: HttpResponse<IUser[]>) => {
        this.users = res.body;
      });
      this.coreParserService.findAll().subscribe((res: HttpResponse<ICoreParser[]>) => {
        this.coreParsers = res.body;
      });
      this.getSignatureImage();
    });
  }
  ngAfterViewInit(): void {
    if (this.isOnlyUser()) {
      this.userDropdown?.nativeElement.setAttribute('readOnly', true);
      this.userDropdown?.nativeElement.setAttribute('disabled', true);
    }
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
    this.updateSignatureTemplate(signatureTemplate);
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

  getSignatureImage(): void {
    const signatureImageCustom = {
      width: this.width === undefined ? 355 : this.width?.nativeElement?.value,
      height: this.height === undefined ? 130 : this.height.nativeElement?.value,
      htmlTemplate: encodeURIComponent(this.editForm.get(['htmlTemplate'])!.value),
    };
    this.signatureTemplateService.getSignatureImageExamp(signatureImageCustom).subscribe((res: any) => {
      this.signatureImageExam = res.body;

      this.signatureImage && this.signatureImage.nativeElement
        ? (this.signatureImage.nativeElement.src = 'data:image/jpeg;base64,' + res.body.data)
        : null;
    });
  }

  private updateSignatureTemplate(signatureTemplate: SignatureTemplate): void {
    signatureTemplate.coreParser = this.coreParserDropdown?.nativeElement.value;
    signatureTemplate.userId = this.userDropdown?.nativeElement.value;
    signatureTemplate.htmlTemplate = this.editForm.get(['htmlTemplate'])!.value;
    signatureTemplate.createdDate = new Date();
  }

  isOnlyUser(): boolean {
    const user = this.accountService.hasAnyAuthority(Authority.USER);
    const admin = this.accountService.hasAnyAuthority(Authority.ADMIN);
    if (user && !admin) return true;
    else return false;
  }
}
