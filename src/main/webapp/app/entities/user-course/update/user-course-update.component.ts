import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IUserCourse, UserCourse } from '../user-course.model';
import { UserCourseService } from '../service/user-course.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-user-course-update',
  templateUrl: './user-course-update.component.html',
})
export class UserCourseUpdateComponent implements OnInit {
  isSaving = false;

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
    userId: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected userCourseService: UserCourseService,
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserCourse>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
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
      userId: userCourse.userId,
    });
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
      userId: this.editForm.get(['userId'])!.value,
    };
  }
}
