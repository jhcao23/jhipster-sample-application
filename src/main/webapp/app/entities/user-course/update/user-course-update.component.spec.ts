jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { UserCourseService } from '../service/user-course.service';
import { IUserCourse, UserCourse } from '../user-course.model';

import { UserCourseUpdateComponent } from './user-course-update.component';

describe('Component Tests', () => {
  describe('UserCourse Management Update Component', () => {
    let comp: UserCourseUpdateComponent;
    let fixture: ComponentFixture<UserCourseUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let userCourseService: UserCourseService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UserCourseUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(UserCourseUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserCourseUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      userCourseService = TestBed.inject(UserCourseService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const userCourse: IUserCourse = { id: 456 };

        activatedRoute.data = of({ userCourse });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(userCourse));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserCourse>>();
        const userCourse = { id: 123 };
        jest.spyOn(userCourseService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userCourse });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userCourse }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(userCourseService.update).toHaveBeenCalledWith(userCourse);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserCourse>>();
        const userCourse = new UserCourse();
        jest.spyOn(userCourseService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userCourse });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userCourse }));
        saveSubject.complete();

        // THEN
        expect(userCourseService.create).toHaveBeenCalledWith(userCourse);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserCourse>>();
        const userCourse = { id: 123 };
        jest.spyOn(userCourseService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userCourse });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(userCourseService.update).toHaveBeenCalledWith(userCourse);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
