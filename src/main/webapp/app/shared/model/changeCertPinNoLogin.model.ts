export interface IChangeCertPinNoLogin {
  masterKey?: string;
  serial?: string;
  oldPin?: string;
  newPin?: string;
  requestType: string;
}
