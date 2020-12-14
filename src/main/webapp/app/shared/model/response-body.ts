export class ResponseBody {
  static SUCCESS = 0;
  constructor(public status: number, public data?: any, public msg?: string) {}
}
