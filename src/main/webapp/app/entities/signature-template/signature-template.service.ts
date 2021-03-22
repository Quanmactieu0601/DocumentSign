import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISignatureTemplate } from 'app/shared/model/signature-template.model';

type EntityResponseType = HttpResponse<ISignatureTemplate>;
type EntityArrayResponseType = HttpResponse<ISignatureTemplate[]>;

@Injectable({ providedIn: 'root' })
export class SignatureTemplateService {
  public resourceUrl = SERVER_API_URL + 'api/signature-templates';

  constructor(protected http: HttpClient) {}

  create(signatureTemplate: ISignatureTemplate): Observable<EntityResponseType> {
    return this.http.post<ISignatureTemplate>(this.resourceUrl, signatureTemplate, { observe: 'response' });
  }

  update(signatureTemplate: ISignatureTemplate): Observable<EntityResponseType> {
    return this.http.put<ISignatureTemplate>(this.resourceUrl, signatureTemplate, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISignatureTemplate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISignatureTemplate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSignatureImageExamp(req?: any): Observable<HttpResponse<any>> {
    const options = createRequestOption(req);
    return this.http.get<any>(this.resourceUrl + '/signExample', { params: options, observe: 'response' });
  }
}
