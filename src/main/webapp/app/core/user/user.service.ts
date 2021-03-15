import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { IUser, User } from './user.model';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class UserService {
  public resourceUrl = SERVER_API_URL + 'api/users';
  public updateP12Url = SERVER_API_URL + 'api/certificate';
  public listId: number[] = [];
  public users: User[] | null = null;

  constructor(private http: HttpClient) {}

  //TODO : add password to request body
  create(user: IUser): Observable<IUser> {
    return this.http.post<IUser>(this.resourceUrl, user);
  }

  update(user: IUser): Observable<IUser> {
    return this.http.put<IUser>(this.resourceUrl, user);
  }

  find(login: string): Observable<IUser> {
    return this.http.get<IUser>(`${this.resourceUrl}/${login}`);
  }

  findByUser(req?: any): Observable<any> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceUrl + '/search', { params: options, observe: 'response' });
  }

  query(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(login: string): Observable<{}> {
    return this.http.delete(`${this.resourceUrl}/${login}`);
  }

  authorities(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/users/authorities');
  }

  upload(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('file', file);
    // formData.append('login', login);
    const req = new HttpRequest('POST', `${this.resourceUrl}/uploadUser`, formData, {
      reportProgress: true,
      responseType: 'json',
    });
    return this.http.request(req);
  }

  uploadP12(file: File, pin: string): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('pin', pin);
    if (this.users !== null && this.users.length >= 1 && this.users[0].login !== undefined) formData.append('ownerId', this.users[0].login);
    const req = new HttpRequest('POST', `${this.updateP12Url}/import/p12file`, formData, {
      reportProgress: true,
      responseType: 'json',
    });
    return this.http.request(req);
  }

  getFiles(): Observable<any> {
    return this.http.get(`${this.resourceUrl}/files`);
  }

  setListId(listIdTrans: number[]): void {
    this.listId = listIdTrans;
  }

  getListId(): any {
    return this.listId;
  }

  setUsers(user: User[]): void {
    this.users = user;
  }

  downLoadTemplateFile(): Observable<any> {
    return this.http.get(`${this.resourceUrl}/templateFile`, { observe: 'body' });
  }
}
