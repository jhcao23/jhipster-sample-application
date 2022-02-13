import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICourse, getCourseIdentifier } from '../course.model';

export type EntityResponseType = HttpResponse<ICourse>;
export type EntityArrayResponseType = HttpResponse<ICourse[]>;

@Injectable({ providedIn: 'root' })
export class CourseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/courses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(course: ICourse): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(course);
    return this.http
      .post<ICourse>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(course: ICourse): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(course);
    return this.http
      .put<ICourse>(`${this.resourceUrl}/${getCourseIdentifier(course) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(course: ICourse): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(course);
    return this.http
      .patch<ICourse>(`${this.resourceUrl}/${getCourseIdentifier(course) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICourse>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICourse[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCourseToCollectionIfMissing(courseCollection: ICourse[], ...coursesToCheck: (ICourse | null | undefined)[]): ICourse[] {
    const courses: ICourse[] = coursesToCheck.filter(isPresent);
    if (courses.length > 0) {
      const courseCollectionIdentifiers = courseCollection.map(courseItem => getCourseIdentifier(courseItem)!);
      const coursesToAdd = courses.filter(courseItem => {
        const courseIdentifier = getCourseIdentifier(courseItem);
        if (courseIdentifier == null || courseCollectionIdentifiers.includes(courseIdentifier)) {
          return false;
        }
        courseCollectionIdentifiers.push(courseIdentifier);
        return true;
      });
      return [...coursesToAdd, ...courseCollection];
    }
    return courseCollection;
  }

  protected convertDateFromClient(course: ICourse): ICourse {
    return Object.assign({}, course, {
      createdDt: course.createdDt?.isValid() ? course.createdDt.toJSON() : undefined,
      startDt: course.startDt?.isValid() ? course.startDt.toJSON() : undefined,
      endDt: course.endDt?.isValid() ? course.endDt.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDt = res.body.createdDt ? dayjs(res.body.createdDt) : undefined;
      res.body.startDt = res.body.startDt ? dayjs(res.body.startDt) : undefined;
      res.body.endDt = res.body.endDt ? dayjs(res.body.endDt) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((course: ICourse) => {
        course.createdDt = course.createdDt ? dayjs(course.createdDt) : undefined;
        course.startDt = course.startDt ? dayjs(course.startDt) : undefined;
        course.endDt = course.endDt ? dayjs(course.endDt) : undefined;
      });
    }
    return res;
  }
}
