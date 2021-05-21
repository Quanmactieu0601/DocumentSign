import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ISignatureTemplate } from 'app/shared/model/signature-template.model';
import { SignatureTemplateService } from './signature-template.service';
import { UserService } from 'app/core/user/user.service';
import { IUser } from 'app/core/user/user.model';
import { ICoreParser } from 'app/shared/model/core-parser.model';
import { CoreParserService } from 'app/entities/core-parser/core-parser.service';
import { AccountService } from 'app/core/auth/account.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { UserPopupComponent } from 'app/entities/signature-template/user-popup/user-popup.component';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-signature-template-update',
  templateUrl: './signature-template-update.component.html',
  styleUrls: ['./signature-template-update.component.scss'],
})
export class SignatureTemplateUpdateComponent implements OnInit, AfterViewInit {
  @ViewChild('signatureImage') signatureImage: ElementRef | undefined;

  isSaving = false;
  user?: IUser | null;
  coreParsers?: ICoreParser[] | null;
  signatureImageExam?: String | null;
  editForm = this.fb.group({
    id: [],
    // signatureImage: [],
    createdDate: [],
    createdBy: [],
    coreParser: [null, [Validators.required]],
    userId: [this.user?.id, [Validators.required]],
    account: [this.user?.login, [Validators.required]],
    htmlTemplate: ['', [Validators.required]],
    width: ['', [Validators.required, Validators.pattern('\\d+')]],
    height: ['', [Validators.required, Validators.pattern('\\d+')]],
    transparency: [],
  });
  modalRef: NgbModalRef | undefined;
  isCreateNew = true;

  constructor(
    protected signatureTemplateService: SignatureTemplateService,
    protected activatedRoute: ActivatedRoute,
    private userService: UserService,
    private coreParserService: CoreParserService,
    protected accountService: AccountService,
    private modalService: NgbModal,
    private fb: FormBuilder,
    private router: Router,
    private toastrService: ToastrService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signatureTemplate }) => {
      if (signatureTemplate.id) {
        this.isCreateNew = false;
      }
      this.updateForm(signatureTemplate);

      if (signatureTemplate.userId) {
        this.userService.findById(signatureTemplate.userId).subscribe(user => {
          this.user = user;
          this.updateAccountInput(user);
        });
      }

      this.coreParserService.findAll().subscribe((res: HttpResponse<ICoreParser[]>) => {
        this.coreParsers = res.body;
      });
      this.getSignatureImage();
    });
  }

  ngAfterViewInit(): void {}

  updateForm(signatureTemplate: ISignatureTemplate): void {
    this.editForm.patchValue(signatureTemplate);
  }

  save(): void {
    this.isSaving = true;
    const data = this.editForm.value;
    if (data.id) {
      this.subscribeToSaveResponse(this.signatureTemplateService.update(data));
    } else {
      this.subscribeToSaveResponse(this.signatureTemplateService.create(data));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISignatureTemplate>>): void {
    result.subscribe(
      () => {
        this.onSaveSuccess();
      },
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.router.navigate(['/home/signature-template']);
    const transKey = this.isCreateNew
      ? 'webappApp.signatureTemplate.alert.success.create'
      : 'webappApp.signatureTemplate.alert.success.update';
    this.toastrService.success(this.translateService.instant(transKey));
  }

  protected onSaveError(): void {
    this.isSaving = false;
    const transKey = this.isCreateNew
      ? 'webappApp.signatureTemplate.alert.failure.create'
      : 'webappApp.signatureTemplate.alert.failure.update';
    this.toastrService.error(this.translateService.instant(transKey));
  }

  getSignatureImage(): void {
    const signatureImageCustom = {
      width: this.editForm.get(['width'])?.value ? this.editForm.get(['width'])!.value : 355,
      height: this.editForm.get(['height'])?.value ? this.editForm.get(['height'])!.value : 130,
      htmlTemplate: this.editForm.get(['htmlTemplate'])!.value,
      transparency: this.editForm.get(['transparency'])!.value,
    };
    this.signatureTemplateService.getSignatureImageExamp(signatureImageCustom).subscribe((res: any) => {
      this.signatureImageExam = res.body;

      this.signatureImage && this.signatureImage.nativeElement
        ? (this.signatureImage.nativeElement.src = 'data:image/jpeg;base64,' + res.body.data)
        : null;
    });
  }

  showUserPopUp(): any {
    this.modalRef = this.modalService.open(UserPopupComponent, { size: 'lg' });
    // this.modalRef.componentInstance.userSelectEvent = this.userSelectEvent;
    this.modalRef.result.then(user => {
      this.user = user;
      this.updateAccountInput(user);
    });
  }

  updateAccountInput(user: IUser): void {
    this.editForm.get('userId')?.setValue(user?.id);
    this.editForm.get('account')?.setValue(user?.login);
  }

  previousState(): void {
    window.history.back();
  }
}
