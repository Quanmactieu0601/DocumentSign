import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MenuToggleService {
  isShowNavBar = true;
  toggleSideBar(): void {
    this.isShowNavBar = !this.isShowNavBar;
  }
}
