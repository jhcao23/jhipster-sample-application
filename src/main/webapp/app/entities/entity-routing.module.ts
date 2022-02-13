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
        path: 'interest',
        data: { pageTitle: 'learnApp.interest.home.title' },
        loadChildren: () => import('./interest/interest.module').then(m => m.InterestModule),
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
      {
        path: 'user-profile-interest',
        data: { pageTitle: 'learnApp.userProfileInterest.home.title' },
        loadChildren: () => import('./user-profile-interest/user-profile-interest.module').then(m => m.UserProfileInterestModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
