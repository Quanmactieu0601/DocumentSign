import * as moment from 'moment';
import { Moment } from 'moment';

export interface ITransaction {
  id?: number;
  api?: string;
  code?: string;
  message?: string;
  data?: string;
  type?: string;
  host?: string;
  method?: string;
  fullName?: string;
  triggerTime?: Moment;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public api?: string,
    public code?: string,
    public message?: string,
    public data?: string,
    public type?: string,
    public host?: string,
    public method?: string,
    public fullName?: string,
    public triggerTime?: Moment
  ) {}
}
