import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserCourse } from '../user-course.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-user-course-detail',
  templateUrl: './user-course-detail.component.html',
})
export class UserCourseDetailComponent implements OnInit {
  userCourse: IUserCourse | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userCourse }) => {
      this.userCourse = userCourse;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
