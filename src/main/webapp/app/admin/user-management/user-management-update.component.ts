import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { LANGUAGES } from 'app/core/language/language.constants';
import { User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
  user!: User;
  languages = LANGUAGES;
  authorities: string[] = [];
  isSaving = false;
  password: any;
  confirmPassword: any;
  editForm = this.fb.group({
    id: [],
    login: [
      '',
      [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    ],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.maxLength(254), Validators.email]],
    commonName: ['', [Validators.maxLength(250)]],
    organizationName: ['', [Validators.maxLength(250)]],
    organizationUnit: ['', [Validators.maxLength(250)]],
    localityName: ['', [Validators.maxLength(250)]],
    stateName: ['', [Validators.maxLength(250)]],
    country: ['', [Validators.minLength(2), Validators.maxLength(2)]],
    ownerId: ['', [Validators.maxLength(50)]],
    phone: ['', [Validators.minLength(5), Validators.maxLength(50)]],
    activated: [],
    langKey: [],
    authorities: [],
    password: [],
    confirmPassword: [],
  });

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private toastrService: ToastrService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      if (user) {
        this.user = user;
        if (this.user.id === undefined) {
          this.user.activated = true;
        }
        this.updateForm(user);
      }
    });
    this.userService.authorities().subscribe(authorities => {
      this.authorities = authorities;
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateUser(this.user);
    if (
      this.user.commonName === '' &&
      this.user.organizationName === '' &&
      this.user.organizationUnit === '' &&
      this.user.stateName === '' &&
      this.user.localityName === '' &&
      this.user.country === ''
    ) {
      this.toastrService.error(this.translateService.instant('userManagement.updated-err'));
      this.onSaveError();
    } else if (this.user.id !== undefined) {
      this.userService.update(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    } else {
      if (this.updatePass()) {
        this.userService.create(this.user).subscribe(
          () => this.onSaveSuccess(),
          () => this.onSaveError()
        );
      } else {
        this.toastrService.error(this.translateService.instant('register.messages.validate.login.notmatch'));
        this.onSaveError();
      }
    }
  }

  private updateForm(user: User): void {
    this.editForm.patchValue({
      id: user.id,
      login: user.login,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      ownerId: user.ownerId,
      phone: user.phone,
      commonName: user.commonName,
      organizationUnit: user.organizationUnit,
      organizationName: user.organizationName,
      localityName: user.localityName,
      stateName: user.stateName,
      country: user.country,
      activated: user.activated,
      langKey: user.langKey,
      authorities: user.authorities,
    });
  }

  private updatePass(): any {
    this.password = this.editForm.get(['password'])!.value;
    this.confirmPassword = this.editForm.get(['confirmPassword'])!.value;
    if (this.password === this.confirmPassword) {
      return true;
    }
    return false;
  }
  private updateUser(user: User): void {
    user.login = this.editForm.get(['login'])!.value;
    user.firstName = this.editForm.get(['firstName'])!.value;
    user.lastName = this.editForm.get(['lastName'])!.value;
    user.email = this.editForm.get(['email'])!.value;
    user.commonName = this.editForm.get(['commonName'])!.value;
    user.organizationName = this.editForm.get(['organizationName'])!.value;
    user.organizationUnit = this.editForm.get(['organizationUnit'])!.value;
    user.localityName = this.editForm.get(['localityName'])!.value;
    user.stateName = this.editForm.get(['stateName'])!.value;
    user.country = this.editForm.get(['country'])!.value;
    user.activated = this.editForm.get(['activated'])!.value;
    user.langKey = this.editForm.get(['langKey'])!.value;
    user.authorities = this.editForm.get(['authorities'])!.value;
    user.ownerId = this.editForm.get(['ownerId'])!.value;
    user.phone = this.editForm.get(['phone'])!.value;
    user.password = this.editForm.get(['password'])!.value;
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.toastrService.success(this.translateService.instant('userManagement.updated'));
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }
}
