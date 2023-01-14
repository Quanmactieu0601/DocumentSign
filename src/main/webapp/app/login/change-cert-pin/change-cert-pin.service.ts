import { Injectable } from '@angular/core';
import {SERVER_API_URL} from "app/app.constants";
import {HttpClient} from "@angular/common/http";
import {IChangeCertPinNoLogin} from "app/shared/model/changeCertPinNoLogin.model";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ChangeCertPinService {

  private resourceUrl = SERVER_API_URL + "api";

  constructor(protected http: HttpClient) { }

  checkAndChangeCertPin = (changeCertPin : IChangeCertPinNoLogin) : Observable<any> => {
    return this.http.put<IChangeCertPinNoLogin>(this.resourceUrl + "/changePinUserNotLogin" , changeCertPin);
  }
}
