import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICertificate } from 'app/shared/model/certificate.model';

type EntityResponseType = HttpResponse<ICertificate>;
type EntityArrayResponseType = HttpResponse<ICertificate[]>;
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({ providedIn: 'root' })
export class CertificateService {
  public resourceUrl = SERVER_API_URL + 'api/certificate';

  constructor(protected http: HttpClient) {}

  create(certificate: ICertificate): Observable<EntityResponseType> {
    return this.http.post<ICertificate>(this.resourceUrl, certificate, { observe: 'response' });
  }

  update(certificate: ICertificate): Observable<EntityResponseType> {
    return this.http.put<ICertificate>(this.resourceUrl, certificate, { observe: 'response' });
  }

  updateActiveStatus(id: number): Observable<EntityResponseType> {
    return this.http.put<ICertificate>(this.resourceUrl + '/update-active-status', id, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICertificate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICertificate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  public uploadFile(fileToUpload: File): Observable<object> {
    const formData = new FormData();
    formData.append('formData', fileToUpload, fileToUpload.name);

    // return this.http.post(this.resourceUrl + '/upload-file-data', _formData, { headers:{'Content-Type': 'undefined'} ,observe: 'response' });
    return this.http.post(this.resourceUrl + '/upload-file-data', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
  }

  sendData(req?: any): Observable<any> {
    const content = JSON.stringify(req);
    return this.http.post(this.resourceUrl + '/exportCsr', content, httpOptions);
  }
}
