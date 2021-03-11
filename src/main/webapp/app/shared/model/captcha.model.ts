export interface ICaptchaModel {
  captchaText?: string;
  captchaImg?: string;
}

export class CaptchaModel implements ICaptchaModel {
  constructor(public captchaText?: string, public captchaImg?: string) {}
}
