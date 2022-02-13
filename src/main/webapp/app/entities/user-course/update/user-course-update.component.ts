import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IUserCourse, UserCourse } from '../user-course.model';
import { UserCourseService } from '../service/user-course.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { CourseType } from 'app/entities/enumerations/course-type.model';

@Component({
  selector: 'jhi-user-course-update',
  templateUrl: './user-course-update.component.html',
})
export class UserCourseUpdateComponent implements OnInit {
  isSaving = false;
  courseTypeValues = Object.keys(CourseType);

  usersSharedCollection: IUser[] = [];
  coursesSharedCollection: ICourse[] = [];

  editForm = this.fb.group({
    id: [],
    code: [],
    courseType: [],
    name: [],
    desc: [],
    url: [],
    cover: [],
    coverContentType: [],
    beginDt: [],
    dueDt: [],
    user: [null, Validators.required],
    course: [null, Validators.required],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected userCourseService: UserCourseService,
    protected userService: UserService,
    protected courseService: CourseService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userCourse }) => {
      if (userCourse.id === undefined) {
        const today = dayjs().startOf('day');
        userCourse.beginDt = today;
        userCourse.dueDt = today;
      }

      this.updateForm(userCourse);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('learnApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userCourse = this.createFromForm();
    if (userCourse.id !== undefined) {
      this.subscribeToSaveResponse(this.userCourseService.update(userCourse));
    } else {
      this.subscribeToSaveResponse(this.userCourseService.create(userCourse));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackCourseById(index: number, item: ICourse): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserCourse>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userCourse: IUserCourse): void {
    this.editForm.patchValue({
      id: userCourse.id,
      code: userCourse.code,
      courseType: userCourse.courseType,
      name: userCourse.name,
      desc: userCourse.desc,
      url: userCourse.url,
      cover: userCourse.cover,
      coverContentType: userCourse.coverContentType,
      beginDt: userCourse.beginDt ? userCourse.beginDt.format(DATE_TIME_FORMAT) : null,
      dueDt: userCourse.dueDt ? userCourse.dueDt.format(DATE_TIME_FORMAT) : null,
      user: userCourse.user,
      course: userCourse.course,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, userCourse.user);
    this.coursesSharedCollection = this.courseService.addCourseToCollectionIfMissing(this.coursesSharedCollection, userCourse.course);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.courseService
      .query()
      .pipe(map((res: HttpResponse<ICourse[]>) => res.body ?? []))
      .pipe(map((courses: ICourse[]) => this.courseService.addCourseToCollectionIfMissing(courses, this.editForm.get('course')!.value)))
      .subscribe((courses: ICourse[]) => (this.coursesSharedCollection = courses));
  }

  protected createFromForm(): IUserCourse {
    return {
      ...new UserCourse(),
      id: this.editForm.get(['id'])!.value,
      code: this.editForm.get(['code'])!.value,
      courseType: this.editForm.get(['courseType'])!.value,
      name: this.editForm.get(['name'])!.value,
      desc: this.editForm.get(['desc'])!.value,
      url: this.editForm.get(['url'])!.value,
      coverContentType: this.editForm.get(['coverContentType'])!.value,
      cover: this.editForm.get(['cover'])!.value,
      beginDt: this.editForm.get(['beginDt'])!.value ? dayjs(this.editForm.get(['beginDt'])!.value, DATE_TIME_FORMAT) : undefined,
      dueDt: this.editForm.get(['dueDt'])!.value ? dayjs(this.editForm.get(['dueDt'])!.value, DATE_TIME_FORMAT) : undefined,
      user: this.editForm.get(['user'])!.value,
      course: this.editForm.get(['course'])!.value,
    };
  }
}
