import { Moment } from 'moment';

export interface ITransaction {
  id?: number;
  api?: string;
  triggerTime?: Moment;
  code?: string;
  message?: string;
  data?: string;
  type?: string;
  host?: string;
  method?: string;
  userID?: number;
  fullName?: string;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public api?: string,
    public triggerTime?: Moment,
    public code?: string,
    public message?: string,
    public data?: string,
    public type?: string,
    public host?: string,
    public method?: string,
    public userID?: number,
    public fullName?: string
  ) {}
}
