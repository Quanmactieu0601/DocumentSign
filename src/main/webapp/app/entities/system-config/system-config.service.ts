import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISystemConfig } from 'app/shared/model/system-config.model';
import { ResponseBody } from 'app/shared/model/response-body';

type EntityResponseType = HttpResponse<ISystemConfig>;
type EntityArrayResponseType = HttpResponse<ISystemConfig[]>;

@Injectable({ providedIn: 'root' })
export class SystemConfigService {
  public resourceUrl = SERVER_API_URL + 'api/system-configs';

  constructor(protected http: HttpClient) {}

  create(systemConfig: ISystemConfig): Observable<EntityResponseType> {
    return this.http.post<ISystemConfig>(this.resourceUrl, systemConfig, { observe: 'response' });
  }

  update(systemConfig: ISystemConfig): Observable<EntityResponseType> {
    return this.http.put<ISystemConfig>(this.resourceUrl, systemConfig, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISystemConfig>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISystemConfig[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  isAuthenOTP(): Observable<ResponseBody> {
    return this.http.get<ResponseBody>(`${this.resourceUrl}/isAuthenOTP`, { observe: 'body' });
  }
}
