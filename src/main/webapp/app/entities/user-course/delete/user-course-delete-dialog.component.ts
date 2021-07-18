import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserCourse } from '../user-course.model';
import { UserCourseService } from '../service/user-course.service';

@Component({
  templateUrl: './user-course-delete-dialog.component.html',
})
export class UserCourseDeleteDialogComponent {
  userCourse?: IUserCourse;

  constructor(protected userCourseService: UserCourseService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userCourseService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
