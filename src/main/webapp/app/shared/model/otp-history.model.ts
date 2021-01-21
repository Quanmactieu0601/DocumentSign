import { Moment } from 'moment';

export interface IOtpHistory {
  id?: number;
  userId?: number;
  comId?: number;
  secretKey?: string;
  otp?: string;
  actionTime?: Moment;
  expireTime?: Moment;
}

export class OtpHistory implements IOtpHistory {
  constructor(
    public id?: number,
    public userId?: number,
    public comId?: number,
    public secretKey?: string,
    public otp?: string,
    public actionTime?: Moment,
    public expireTime?: Moment
  ) {}
}
