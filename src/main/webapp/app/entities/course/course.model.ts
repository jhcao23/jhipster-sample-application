import * as dayjs from 'dayjs';
import { CourseType } from 'app/entities/enumerations/course-type.model';

export interface ICourse {
  id?: number;
  code?: string | null;
  courseType?: CourseType | null;
  name?: string | null;
  desc?: string | null;
  url?: string | null;
  coverContentType?: string | null;
  cover?: string | null;
  version?: string | null;
  createdDt?: dayjs.Dayjs | null;
  createdBy?: string | null;
  startDt?: dayjs.Dayjs | null;
  endDt?: dayjs.Dayjs | null;
}

export class Course implements ICourse {
  constructor(
    public id?: number,
    public code?: string | null,
    public courseType?: CourseType | null,
    public name?: string | null,
    public desc?: string | null,
    public url?: string | null,
    public coverContentType?: string | null,
    public cover?: string | null,
    public version?: string | null,
    public createdDt?: dayjs.Dayjs | null,
    public createdBy?: string | null,
    public startDt?: dayjs.Dayjs | null,
    public endDt?: dayjs.Dayjs | null
  ) {}
}

export function getCourseIdentifier(course: ICourse): number | undefined {
  return course.id;
}
