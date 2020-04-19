import { environment } from '../environments/environment';
import { Injectable } from '@angular/core';

@Injectable()
export class AppService {
  env: any = environment;
  private _baseUrl: string = this.env.envName === 'dev' ? this.env.baseUrl : window.location.protocol + '//' + window.location.host;
  private _baseUrlWS: string = this.env.envName === 'dev' ? this.env.baseUrlWS : 'ws://' + window.location.host;

  constructor() {}

  get baseUrl() {
    return this._baseUrl;
  }

  get baseUrlWS() {
    return this._baseUrlWS;
  }
}
