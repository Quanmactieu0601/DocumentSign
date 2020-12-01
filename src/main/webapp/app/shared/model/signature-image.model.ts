export interface ISignatureImage {
  id?: number;
  imgData?: string;
  userId?: number;
}

export class SignatureImage implements ISignatureImage {
  constructor(public id?: number, public imgData?: string, public userId?: number) {}
}
