import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICourse, Course } from '../course.model';
import { CourseService } from '../service/course.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { CourseType } from 'app/entities/enumerations/course-type.model';

@Component({
  selector: 'jhi-course-update',
  templateUrl: './course-update.component.html',
})
export class CourseUpdateComponent implements OnInit {
  isSaving = false;
  courseTypeValues = Object.keys(CourseType);

  editForm = this.fb.group({
    id: [],
    code: [],
    courseType: [],
    name: [],
    desc: [],
    url: [],
    cover: [],
    coverContentType: [],
    version: [],
    createdDt: [],
    createdBy: [],
    startDt: [],
    endDt: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected courseService: CourseService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ course }) => {
      if (course.id === undefined) {
        const today = dayjs().startOf('day');
        course.createdDt = today;
        course.startDt = today;
        course.endDt = today;
      }

      this.updateForm(course);
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
    const course = this.createFromForm();
    if (course.id !== undefined) {
      this.subscribeToSaveResponse(this.courseService.update(course));
    } else {
      this.subscribeToSaveResponse(this.courseService.create(course));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICourse>>): void {
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

  protected updateForm(course: ICourse): void {
    this.editForm.patchValue({
      id: course.id,
      code: course.code,
      courseType: course.courseType,
      name: course.name,
      desc: course.desc,
      url: course.url,
      cover: course.cover,
      coverContentType: course.coverContentType,
      version: course.version,
      createdDt: course.createdDt ? course.createdDt.format(DATE_TIME_FORMAT) : null,
      createdBy: course.createdBy,
      startDt: course.startDt ? course.startDt.format(DATE_TIME_FORMAT) : null,
      endDt: course.endDt ? course.endDt.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): ICourse {
    return {
      ...new Course(),
      id: this.editForm.get(['id'])!.value,
      code: this.editForm.get(['code'])!.value,
      courseType: this.editForm.get(['courseType'])!.value,
      name: this.editForm.get(['name'])!.value,
      desc: this.editForm.get(['desc'])!.value,
      url: this.editForm.get(['url'])!.value,
      coverContentType: this.editForm.get(['coverContentType'])!.value,
      cover: this.editForm.get(['cover'])!.value,
      version: this.editForm.get(['version'])!.value,
      createdDt: this.editForm.get(['createdDt'])!.value ? dayjs(this.editForm.get(['createdDt'])!.value, DATE_TIME_FORMAT) : undefined,
      createdBy: this.editForm.get(['createdBy'])!.value,
      startDt: this.editForm.get(['startDt'])!.value ? dayjs(this.editForm.get(['startDt'])!.value, DATE_TIME_FORMAT) : undefined,
      endDt: this.editForm.get(['endDt'])!.value ? dayjs(this.editForm.get(['endDt'])!.value, DATE_TIME_FORMAT) : undefined,
    };
  }
}
