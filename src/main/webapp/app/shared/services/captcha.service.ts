import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ICaptchaModel } from 'app/shared/model/captcha.model';

type EntityResponseType = HttpResponse<ICaptchaModel>;
@Injectable({
  providedIn: 'root',
})
export class CaptchaService {
  public resourceUrl = SERVER_API_URL + 'util/captcha';

  constructor(protected http: HttpClient) {}

  generateCaptcha(): Observable<EntityResponseType> {
    return this.http.get<ICaptchaModel>(this.resourceUrl, { observe: 'response' });
  }
}
