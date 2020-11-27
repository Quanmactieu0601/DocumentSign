import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISignatureImage } from 'app/shared/model/signature-image.model';

type EntityResponseType = HttpResponse<ISignatureImage>;
type EntityArrayResponseType = HttpResponse<ISignatureImage[]>;

@Injectable({ providedIn: 'root' })
export class SignatureImageService {
  public resourceUrl = SERVER_API_URL + 'api/signature-images';

  constructor(protected http: HttpClient) {}

  create(signatureImage: ISignatureImage): Observable<EntityResponseType> {
    return this.http.post<ISignatureImage>(this.resourceUrl, signatureImage, { observe: 'response' });
  }

  update(signatureImage: ISignatureImage): Observable<EntityResponseType> {
    return this.http.put<ISignatureImage>(this.resourceUrl, signatureImage, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISignatureImage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISignatureImage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
