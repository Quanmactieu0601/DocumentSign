import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ChangeCertPinService} from "app/login/change-cert-pin/change-cert-pin.service";
import {IChangeCertPinNoLogin} from "app/shared/model/changeCertPinNoLogin.model";
import {FormBuilder, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {TranslateService} from "@ngx-translate/core";
import {Observable} from "rxjs";

@Component({
  selector: 'jhi-change-cert-pin',
  templateUrl: './change-cert-pin.component.html',
  styleUrls: ['./change-cert-pin.component.scss']
})
export class ChangeCertPinComponent implements OnInit {

  public isChangePin = false;

  public pinForm = this.formBuilder.group({
    currentPin: ['', [Validators.required]],
    newPin: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(50)]],
    confirmPin: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(50)]],
    serial : ['', [Validators.required, Validators.minLength(0)]]
  });


  constructor(
    public activeModal: NgbActiveModal,
    public changeCertPinService: ChangeCertPinService,
    public formBuilder : FormBuilder,
    private toastService: ToastrService,
    private translate : TranslateService) {
  }

  ngOnInit(): void {
  }

  checkMasterKey = (value: string) => {
    const checkMasterKey: IChangeCertPinNoLogin = {
      masterKey: value,
      requestType: "request"
    }
    this.changeCertPinService.checkAndChangeCertPin(checkMasterKey).subscribe(result => {
      if (result.status === 0) {
        this.isChangePin = true;
      } else {
        this.isChangePin = false;
        this.toastService.error(result.msg);
      }
    });
  }

  changePin = () : void => {
    const serialChangePin = this.pinForm.get(['serial'])!.value;
    const currentPIN = this.pinForm.get(['currentPin'])!.value;
    const newPIN = this.pinForm.get(['newPin'])!.value;
    if (newPIN !== this.pinForm.get(['confirmPin'])!.value) {
      this.toastService.error(this.translate.instant('webappApp.certificate.changeCertPIN.error'));
    } else {
      const changePin : IChangeCertPinNoLogin = {
        serial : serialChangePin,
        oldPin : currentPIN,
        newPin : newPIN,
        requestType: "confirm"
      }
      this.changeCertPinService.checkAndChangeCertPin(changePin).subscribe(res => {
        if (res.status !== 0) {
          this.toastService.error(res.msg);
        } else {
          this.toastService.success(this.translate.instant('webappApp.certificate.changeCertPIN.success'));
          this.activeModal.dismiss("done");
        }
      });
    }
  }
}
