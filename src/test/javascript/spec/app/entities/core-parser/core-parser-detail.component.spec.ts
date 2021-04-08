import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { CoreParserDetailComponent } from 'app/entities/core-parser/core-parser-detail.component';
import { CoreParser } from 'app/shared/model/core-parser.model';

describe('Component Tests', () => {
  describe('CoreParser Management Detail Component', () => {
    let comp: CoreParserDetailComponent;
    let fixture: ComponentFixture<CoreParserDetailComponent>;
    const route = ({ data: of({ coreParser: new CoreParser(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [CoreParserDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(CoreParserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CoreParserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load coreParser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.coreParser).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
