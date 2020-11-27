export interface ISignatureTemplate {
  id?: number;
  signatureImage?: string;
  userId?: number;
}

export class SignatureTemplate implements ISignatureTemplate {
  constructor(public id?: number, public signatureImage?: string, public userId?: number) {}
}
