import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MenuToggleService {
  sideBarEnabled = true;
  toggleSideBar(): void {
    this.sideBarEnabled = !this.sideBarEnabled;
  }
}
