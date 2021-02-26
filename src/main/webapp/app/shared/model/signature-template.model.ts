export interface ISignatureTemplate {
  id?: number;
  createdBy?: string;
  coreParser?: string;
  createdDate?: Date;
  htmlTemplate?: string;
  userId?: number;
}

export class SignatureTemplate implements ISignatureTemplate {
  constructor(
    public id?: number,
    public htmlTemplate?: string,
    public userId?: number,
    public createdDate?: Date,
    public createdBy?: string,
    public coreParser?: string
  ) {}
}
