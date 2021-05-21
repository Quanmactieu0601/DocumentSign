export interface IElementVM {
  base64Signature?: string;
  base64OriginalData?: string;
  key?: string;
}

export class ElementVM implements IElementVM {
  constructor(public base64Signature?: string, public base64OriginalData?: string, public key?: string) {}
}
