import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICertPackage } from 'app/shared/model/cert-package.model';

type EntityResponseType = HttpResponse<ICertPackage>;
type EntityArrayResponseType = HttpResponse<ICertPackage[]>;

@Injectable({ providedIn: 'root' })
export class CertPackageService {
  public resourceUrl = SERVER_API_URL + 'api/cert-packages';

  constructor(protected http: HttpClient) {}

  create(certPackage: ICertPackage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(certPackage);
    return this.http
      .post<ICertPackage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(certPackage: ICertPackage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(certPackage);
    return this.http
      .put<ICertPackage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICertPackage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICertPackage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(certPackage: ICertPackage): ICertPackage {
    const copy: ICertPackage = Object.assign({}, certPackage, {
      expiredDate: certPackage.expiredDate && certPackage.expiredDate.isValid() ? certPackage.expiredDate.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.expiredDate = res.body.expiredDate ? moment(res.body.expiredDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((certPackage: ICertPackage) => {
        certPackage.expiredDate = certPackage.expiredDate ? moment(certPackage.expiredDate) : undefined;
      });
    }
    return res;
  }
}
