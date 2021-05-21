import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SystemConfigCategoryDetailComponent } from 'app/entities/system-config-category/system-config-category-detail.component';
import { SystemConfigCategory } from 'app/shared/model/system-config-category.model';

describe('Component Tests', () => {
  describe('SystemConfigCategory Management Detail Component', () => {
    let comp: SystemConfigCategoryDetailComponent;
    let fixture: ComponentFixture<SystemConfigCategoryDetailComponent>;
    const route = ({ data: of({ systemConfigCategory: new SystemConfigCategory(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SystemConfigCategoryDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(SystemConfigCategoryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SystemConfigCategoryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load systemConfigCategory on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.systemConfigCategory).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
