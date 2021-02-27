import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISystemConfigCategory } from 'app/shared/model/system-config-category.model';

type EntityResponseType = HttpResponse<ISystemConfigCategory>;
type EntityArrayResponseType = HttpResponse<ISystemConfigCategory[]>;

@Injectable({ providedIn: 'root' })
export class SystemConfigCategoryService {
  public resourceUrl = SERVER_API_URL + 'api/system-config-categories';

  constructor(protected http: HttpClient) {}

  create(systemConfigCategory: ISystemConfigCategory): Observable<EntityResponseType> {
    return this.http.post<ISystemConfigCategory>(this.resourceUrl, systemConfigCategory, { observe: 'response' });
  }

  update(systemConfigCategory: ISystemConfigCategory): Observable<EntityResponseType> {
    return this.http.put<ISystemConfigCategory>(this.resourceUrl, systemConfigCategory, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISystemConfigCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISystemConfigCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
