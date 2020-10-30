import { Moment } from 'moment';

export interface ITransaction {
  id?: number;
  api?: string;
  triggerTime?: Moment;
  code?: string;
  message?: string;
  data?: string;
  type?: number;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public api?: string,
    public triggerTime?: Moment,
    public code?: string,
    public message?: string,
    public data?: string,
    public type?: number
  ) {}
}
