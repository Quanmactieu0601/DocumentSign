import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHeaders, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICertificate } from 'app/shared/model/certificate.model';
import { ResponseBody } from 'app/shared/model/response-body';

type EntityResponseType = HttpResponse<ICertificate>;
type EntityArrayResponseType = HttpResponse<ICertificate[]>;
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  responseType: 'arraybuffer' as 'arraybuffer',
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

  // findByUserLogin(user: IUser): Observable<EntityArrayResponseType> {
  //   const option = createRequestOption(user)
  //   return this.http.get<ICertificate[]>(this.resourceUrl + 'search', { params: option, observe: 'response' });
  // }

  findByCurrentUser(userId: number): Observable<EntityArrayResponseType> {
    return this.http.get<ICertificate[]>(`${this.resourceUrl + '/ownerId'}/${userId}`, { observe: 'response' });
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
    return this.http.post(this.resourceUrl + '/exportCsr', req, httpOptions);
  }
  upload(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/uploadCert`, formData, {
      reportProgress: true,
      responseType: 'json',
    });

    return this.http.request(req);
  }

  uploadP12(files: File[]): Observable<any> {
    const formData: FormData = new FormData();

    Array.from(files).forEach(file => {
      formData.append('files', file);
    });

    const req = new HttpRequest('POST', `api/data/importP12FileSelected`, formData, {
      responseType: 'arraybuffer' as 'arraybuffer',
    });

    return this.http.request(req);
  }

  getFiles(): Observable<any> {
    return this.http.get(`${this.resourceUrl}/files`);
  }

  findCertificate(req?: any): Observable<any> {
    const options = createRequestOption(req);
    return this.http.get<ICertificate[]>(this.resourceUrl + '/search', { params: options, observe: 'response' });
  }

  getQRCodeOTP(req?: any): Observable<ResponseBody> {
    const options = createRequestOption(req);
    return this.http.get<ResponseBody>(`${this.resourceUrl}/getQRCodeOTP`, { params: options, observe: 'body' });
  }
}
