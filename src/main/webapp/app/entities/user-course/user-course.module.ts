import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserCourseComponent } from './list/user-course.component';
import { UserCourseDetailComponent } from './detail/user-course-detail.component';
import { UserCourseUpdateComponent } from './update/user-course-update.component';
import { UserCourseDeleteDialogComponent } from './delete/user-course-delete-dialog.component';
import { UserCourseRoutingModule } from './route/user-course-routing.module';

@NgModule({
  imports: [SharedModule, UserCourseRoutingModule],
  declarations: [UserCourseComponent, UserCourseDetailComponent, UserCourseUpdateComponent, UserCourseDeleteDialogComponent],
  entryComponents: [UserCourseDeleteDialogComponent],
})
export class UserCourseModule {}
