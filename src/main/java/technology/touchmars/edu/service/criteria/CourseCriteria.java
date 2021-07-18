package technology.touchmars.edu.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;
import technology.touchmars.edu.domain.enumeration.CourseType;

/**
 * Criteria class for the {@link technology.touchmars.edu.domain.Course} entity. This class is used
 * in {@link technology.touchmars.edu.web.rest.CourseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /courses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CourseCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CourseType
     */
    public static class CourseTypeFilter extends Filter<CourseType> {

        public CourseTypeFilter() {}

        public CourseTypeFilter(CourseTypeFilter filter) {
            super(filter);
        }

        @Override
        public CourseTypeFilter copy() {
            return new CourseTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private CourseTypeFilter courseType;

    private StringFilter name;

    private StringFilter url;

    private StringFilter version;

    private ZonedDateTimeFilter createdDt;

    private StringFilter createdBy;

    private ZonedDateTimeFilter startDt;

    private ZonedDateTimeFilter endDt;

    public CourseCriteria() {}

    public CourseCriteria(CourseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.courseType = other.courseType == null ? null : other.courseType.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.createdDt = other.createdDt == null ? null : other.createdDt.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.startDt = other.startDt == null ? null : other.startDt.copy();
        this.endDt = other.endDt == null ? null : other.endDt.copy();
    }

    @Override
    public CourseCriteria copy() {
        return new CourseCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public StringFilter code() {
        if (code == null) {
            code = new StringFilter();
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public CourseTypeFilter getCourseType() {
        return courseType;
    }

    public CourseTypeFilter courseType() {
        if (courseType == null) {
            courseType = new CourseTypeFilter();
        }
        return courseType;
    }

    public void setCourseType(CourseTypeFilter courseType) {
        this.courseType = courseType;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getVersion() {
        return version;
    }

    public StringFilter version() {
        if (version == null) {
            version = new StringFilter();
        }
        return version;
    }

    public void setVersion(StringFilter version) {
        this.version = version;
    }

    public ZonedDateTimeFilter getCreatedDt() {
        return createdDt;
    }

    public ZonedDateTimeFilter createdDt() {
        if (createdDt == null) {
            createdDt = new ZonedDateTimeFilter();
        }
        return createdDt;
    }

    public void setCreatedDt(ZonedDateTimeFilter createdDt) {
        this.createdDt = createdDt;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTimeFilter getStartDt() {
        return startDt;
    }

    public ZonedDateTimeFilter startDt() {
        if (startDt == null) {
            startDt = new ZonedDateTimeFilter();
        }
        return startDt;
    }

    public void setStartDt(ZonedDateTimeFilter startDt) {
        this.startDt = startDt;
    }

    public ZonedDateTimeFilter getEndDt() {
        return endDt;
    }

    public ZonedDateTimeFilter endDt() {
        if (endDt == null) {
            endDt = new ZonedDateTimeFilter();
        }
        return endDt;
    }

    public void setEndDt(ZonedDateTimeFilter endDt) {
        this.endDt = endDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CourseCriteria that = (CourseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(courseType, that.courseType) &&
            Objects.equals(name, that.name) &&
            Objects.equals(url, that.url) &&
            Objects.equals(version, that.version) &&
            Objects.equals(createdDt, that.createdDt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(startDt, that.startDt) &&
            Objects.equals(endDt, that.endDt)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, courseType, name, url, version, createdDt, createdBy, startDt, endDt);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (courseType != null ? "courseType=" + courseType + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (version != null ? "version=" + version + ", " : "") +
            (createdDt != null ? "createdDt=" + createdDt + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (startDt != null ? "startDt=" + startDt + ", " : "") +
            (endDt != null ? "endDt=" + endDt + ", " : "") +
            "}";
    }
}
