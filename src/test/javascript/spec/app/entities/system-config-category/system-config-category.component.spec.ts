import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { WebappTestModule } from '../../../test.module';
import { SystemConfigCategoryComponent } from 'app/entities/system-config-category/system-config-category.component';
import { SystemConfigCategoryService } from 'app/entities/system-config-category/system-config-category.service';
import { SystemConfigCategory } from 'app/shared/model/system-config-category.model';

describe('Component Tests', () => {
  describe('SystemConfigCategory Management Component', () => {
    let comp: SystemConfigCategoryComponent;
    let fixture: ComponentFixture<SystemConfigCategoryComponent>;
    let service: SystemConfigCategoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SystemConfigCategoryComponent],
      })
        .overrideTemplate(SystemConfigCategoryComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SystemConfigCategoryComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SystemConfigCategoryService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new SystemConfigCategory(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.systemConfigCategories && comp.systemConfigCategories[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
