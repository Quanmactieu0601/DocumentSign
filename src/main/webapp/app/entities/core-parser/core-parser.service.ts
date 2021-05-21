import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICoreParser } from 'app/shared/model/core-parser.model';

type EntityResponseType = HttpResponse<ICoreParser>;
type EntityArrayResponseType = HttpResponse<ICoreParser[]>;

@Injectable({ providedIn: 'root' })
export class CoreParserService {
  public resourceUrl = SERVER_API_URL + 'api/core-parsers';
  constructor(protected http: HttpClient) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICoreParser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICoreParser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  findAll(): Observable<HttpResponse<ICoreParser[]>> {
    return this.http.get<ICoreParser[]>(this.resourceUrl + '/getAll', { observe: 'response' });
  }
}
