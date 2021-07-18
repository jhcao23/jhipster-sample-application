import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserCourse, UserCourse } from '../user-course.model';
import { UserCourseService } from '../service/user-course.service';

@Injectable({ providedIn: 'root' })
export class UserCourseRoutingResolveService implements Resolve<IUserCourse> {
  constructor(protected service: UserCourseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserCourse> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userCourse: HttpResponse<UserCourse>) => {
          if (userCourse.body) {
            return of(userCourse.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserCourse());
  }
}
