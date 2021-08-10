import { ActivatedRoute, Router } from '@angular/router';
import { Injectable } from '@angular/core';
@Injectable({
  providedIn: 'root',
})
export class RefreshPage {
  constructor(private router: Router) {}

  refresh(): void {
    const currentUrl = this.router.url;
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate([currentUrl]);
  }
}
