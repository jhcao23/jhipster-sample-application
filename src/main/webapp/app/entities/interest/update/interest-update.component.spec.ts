import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InterestService } from '../service/interest.service';
import { IInterest, Interest } from '../interest.model';

import { InterestUpdateComponent } from './interest-update.component';

describe('Interest Management Update Component', () => {
  let comp: InterestUpdateComponent;
  let fixture: ComponentFixture<InterestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let interestService: InterestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InterestUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(InterestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InterestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    interestService = TestBed.inject(InterestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const interest: IInterest = { id: 456 };

      activatedRoute.data = of({ interest });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(interest));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Interest>>();
      const interest = { id: 123 };
      jest.spyOn(interestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: interest }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(interestService.update).toHaveBeenCalledWith(interest);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Interest>>();
      const interest = new Interest();
      jest.spyOn(interestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: interest }));
      saveSubject.complete();

      // THEN
      expect(interestService.create).toHaveBeenCalledWith(interest);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Interest>>();
      const interest = { id: 123 };
      jest.spyOn(interestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(interestService.update).toHaveBeenCalledWith(interest);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
