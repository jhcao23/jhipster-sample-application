import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { CourseType } from 'app/entities/enumerations/course-type.model';
import { IUserCourse, UserCourse } from '../user-course.model';

import { UserCourseService } from './user-course.service';

describe('UserCourse Service', () => {
  let service: UserCourseService;
  let httpMock: HttpTestingController;
  let elemDefault: IUserCourse;
  let expectedResult: IUserCourse | IUserCourse[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserCourseService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      code: 'AAAAAAA',
      courseType: CourseType.PROGRAM,
      name: 'AAAAAAA',
      desc: 'AAAAAAA',
      url: 'AAAAAAA',
      coverContentType: 'image/png',
      cover: 'AAAAAAA',
      beginDt: currentDate,
      dueDt: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          beginDt: currentDate.format(DATE_TIME_FORMAT),
          dueDt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a UserCourse', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          beginDt: currentDate.format(DATE_TIME_FORMAT),
          dueDt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          beginDt: currentDate,
          dueDt: currentDate,
        },
        returnedFromService
      );

      service.create(new UserCourse()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserCourse', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          code: 'BBBBBB',
          courseType: 'BBBBBB',
          name: 'BBBBBB',
          desc: 'BBBBBB',
          url: 'BBBBBB',
          cover: 'BBBBBB',
          beginDt: currentDate.format(DATE_TIME_FORMAT),
          dueDt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          beginDt: currentDate,
          dueDt: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserCourse', () => {
      const patchObject = Object.assign(
        {
          courseType: 'BBBBBB',
          desc: 'BBBBBB',
          url: 'BBBBBB',
          cover: 'BBBBBB',
          dueDt: currentDate.format(DATE_TIME_FORMAT),
        },
        new UserCourse()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          beginDt: currentDate,
          dueDt: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserCourse', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          code: 'BBBBBB',
          courseType: 'BBBBBB',
          name: 'BBBBBB',
          desc: 'BBBBBB',
          url: 'BBBBBB',
          cover: 'BBBBBB',
          beginDt: currentDate.format(DATE_TIME_FORMAT),
          dueDt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          beginDt: currentDate,
          dueDt: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a UserCourse', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUserCourseToCollectionIfMissing', () => {
      it('should add a UserCourse to an empty array', () => {
        const userCourse: IUserCourse = { id: 123 };
        expectedResult = service.addUserCourseToCollectionIfMissing([], userCourse);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userCourse);
      });

      it('should not add a UserCourse to an array that contains it', () => {
        const userCourse: IUserCourse = { id: 123 };
        const userCourseCollection: IUserCourse[] = [
          {
            ...userCourse,
          },
          { id: 456 },
        ];
        expectedResult = service.addUserCourseToCollectionIfMissing(userCourseCollection, userCourse);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserCourse to an array that doesn't contain it", () => {
        const userCourse: IUserCourse = { id: 123 };
        const userCourseCollection: IUserCourse[] = [{ id: 456 }];
        expectedResult = service.addUserCourseToCollectionIfMissing(userCourseCollection, userCourse);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userCourse);
      });

      it('should add only unique UserCourse to an array', () => {
        const userCourseArray: IUserCourse[] = [{ id: 123 }, { id: 456 }, { id: 95254 }];
        const userCourseCollection: IUserCourse[] = [{ id: 123 }];
        expectedResult = service.addUserCourseToCollectionIfMissing(userCourseCollection, ...userCourseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userCourse: IUserCourse = { id: 123 };
        const userCourse2: IUserCourse = { id: 456 };
        expectedResult = service.addUserCourseToCollectionIfMissing([], userCourse, userCourse2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userCourse);
        expect(expectedResult).toContain(userCourse2);
      });

      it('should accept null and undefined values', () => {
        const userCourse: IUserCourse = { id: 123 };
        expectedResult = service.addUserCourseToCollectionIfMissing([], null, userCourse, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userCourse);
      });

      it('should return initial array if no UserCourse is added', () => {
        const userCourseCollection: IUserCourse[] = [{ id: 123 }];
        expectedResult = service.addUserCourseToCollectionIfMissing(userCourseCollection, undefined, null);
        expect(expectedResult).toEqual(userCourseCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
