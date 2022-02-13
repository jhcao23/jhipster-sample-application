import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserCourseService } from '../service/user-course.service';
import { IUserCourse, UserCourse } from '../user-course.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';

import { UserCourseUpdateComponent } from './user-course-update.component';

describe('UserCourse Management Update Component', () => {
  let comp: UserCourseUpdateComponent;
  let fixture: ComponentFixture<UserCourseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userCourseService: UserCourseService;
  let userService: UserService;
  let courseService: CourseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserCourseUpdateComponent],
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
      .overrideTemplate(UserCourseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserCourseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userCourseService = TestBed.inject(UserCourseService);
    userService = TestBed.inject(UserService);
    courseService = TestBed.inject(CourseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userCourse: IUserCourse = { id: 456 };
      const user: IUser = { id: 72320 };
      userCourse.user = user;

      const userCollection: IUser[] = [{ id: 27783 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userCourse });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Course query and add missing value', () => {
      const userCourse: IUserCourse = { id: 456 };
      const course: ICourse = { id: 82215 };
      userCourse.course = course;

      const courseCollection: ICourse[] = [{ id: 46723 }];
      jest.spyOn(courseService, 'query').mockReturnValue(of(new HttpResponse({ body: courseCollection })));
      const additionalCourses = [course];
      const expectedCollection: ICourse[] = [...additionalCourses, ...courseCollection];
      jest.spyOn(courseService, 'addCourseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userCourse });
      comp.ngOnInit();

      expect(courseService.query).toHaveBeenCalled();
      expect(courseService.addCourseToCollectionIfMissing).toHaveBeenCalledWith(courseCollection, ...additionalCourses);
      expect(comp.coursesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userCourse: IUserCourse = { id: 456 };
      const user: IUser = { id: 55468 };
      userCourse.user = user;
      const course: ICourse = { id: 81558 };
      userCourse.course = course;

      activatedRoute.data = of({ userCourse });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(userCourse));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.coursesSharedCollection).toContain(course);
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

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCourseById', () => {
      it('Should return tracked Course primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCourseById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
