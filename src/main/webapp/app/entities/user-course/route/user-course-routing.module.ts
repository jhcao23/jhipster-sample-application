import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserCourseComponent } from '../list/user-course.component';
import { UserCourseDetailComponent } from '../detail/user-course-detail.component';
import { UserCourseUpdateComponent } from '../update/user-course-update.component';
import { UserCourseRoutingResolveService } from './user-course-routing-resolve.service';

const userCourseRoute: Routes = [
  {
    path: '',
    component: UserCourseComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserCourseDetailComponent,
    resolve: {
      userCourse: UserCourseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserCourseUpdateComponent,
    resolve: {
      userCourse: UserCourseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserCourseUpdateComponent,
    resolve: {
      userCourse: UserCourseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userCourseRoute)],
  exports: [RouterModule],
})
export class UserCourseRoutingModule {}
