import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICoreParser } from 'app/shared/model/core-parser.model';

@Component({
  selector: 'jhi-core-parser-detail',
  templateUrl: './core-parser-detail.component.html',
})
export class CoreParserDetailComponent implements OnInit {
  coreParser: ICoreParser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coreParser }) => (this.coreParser = coreParser));
  }

  previousState(): void {
    window.history.back();
  }
}
