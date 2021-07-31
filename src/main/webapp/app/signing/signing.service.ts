import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  responseType: 'text' as 'text',
};
@Injectable({ providedIn: 'root' })
export class SigningService {
  public resourceURL = SERVER_API_URL + 'api/sign';

  constructor(protected http: HttpClient) {}

  signDocInvisible(req: any): Observable<any> {
    const requestData = JSON.stringify(req);
    return this.http.post(this.resourceURL + '/office', requestData, httpOptions);
  }

  signPdf(req: any): Observable<any> {
    const requestData = JSON.stringify(req);
    return this.http.post(this.resourceURL + '/pdf', requestData, httpOptions);
  }
}
