import { Moment } from 'moment';

export interface ITransaction {
  id?: number;
  api?: string;
  status?: boolean;
  message?: string;
  data?: string;
  type?: string;
  host?: string;
  method?: string;
  fullName?: string;
  triggerTime?: Moment;
  action?: string;
  extension?: string;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public api?: string,
    public status?: boolean,
    public message?: string,
    public data?: string,
    public type?: string,
    public host?: string,
    public method?: string,
    public fullName?: string,
    public triggerTime?: Moment,
    public action?: string,
    public extension?: string
  ) {}
}
