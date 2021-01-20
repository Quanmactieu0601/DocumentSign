import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IOtpHistory } from 'app/shared/model/otp-history.model';

type EntityResponseType = HttpResponse<IOtpHistory>;
type EntityArrayResponseType = HttpResponse<IOtpHistory[]>;

@Injectable({ providedIn: 'root' })
export class OtpHistoryService {
  public resourceUrl = SERVER_API_URL + 'api/otp-histories';

  constructor(protected http: HttpClient) {}

  create(otpHistory: IOtpHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(otpHistory);
    return this.http
      .post<IOtpHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(otpHistory: IOtpHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(otpHistory);
    return this.http
      .put<IOtpHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOtpHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOtpHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(otpHistory: IOtpHistory): IOtpHistory {
    const copy: IOtpHistory = Object.assign({}, otpHistory, {
      actionTime: otpHistory.actionTime && otpHistory.actionTime.isValid() ? otpHistory.actionTime.format(DATE_FORMAT) : undefined,
      expireTime: otpHistory.expireTime && otpHistory.expireTime.isValid() ? otpHistory.expireTime.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.actionTime = res.body.actionTime ? moment(res.body.actionTime) : undefined;
      res.body.expireTime = res.body.expireTime ? moment(res.body.expireTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((otpHistory: IOtpHistory) => {
        otpHistory.actionTime = otpHistory.actionTime ? moment(otpHistory.actionTime) : undefined;
        otpHistory.expireTime = otpHistory.expireTime ? moment(otpHistory.expireTime) : undefined;
      });
    }
    return res;
  }
}
