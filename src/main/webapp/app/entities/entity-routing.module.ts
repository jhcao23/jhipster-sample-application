import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'user-profile',
        data: { pageTitle: 'learnApp.userProfile.home.title' },
        loadChildren: () => import('./user-profile/user-profile.module').then(m => m.UserProfileModule),
      },
      {
        path: 'course',
        data: { pageTitle: 'learnApp.course.home.title' },
        loadChildren: () => import('./course/course.module').then(m => m.CourseModule),
      },
      {
        path: 'user-course',
        data: { pageTitle: 'learnApp.userCourse.home.title' },
        loadChildren: () => import('./user-course/user-course.module').then(m => m.UserCourseModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
