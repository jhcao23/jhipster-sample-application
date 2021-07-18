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
 * Criteria class for the {@link technology.touchmars.edu.domain.UserCourse} entity. This class is used
 * in {@link technology.touchmars.edu.web.rest.UserCourseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-courses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserCourseCriteria implements Serializable, Criteria {

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

    private ZonedDateTimeFilter beginDt;

    private ZonedDateTimeFilter dueDt;

    private StringFilter userId;

    public UserCourseCriteria() {}

    public UserCourseCriteria(UserCourseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.courseType = other.courseType == null ? null : other.courseType.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.beginDt = other.beginDt == null ? null : other.beginDt.copy();
        this.dueDt = other.dueDt == null ? null : other.dueDt.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public UserCourseCriteria copy() {
        return new UserCourseCriteria(this);
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

    public ZonedDateTimeFilter getBeginDt() {
        return beginDt;
    }

    public ZonedDateTimeFilter beginDt() {
        if (beginDt == null) {
            beginDt = new ZonedDateTimeFilter();
        }
        return beginDt;
    }

    public void setBeginDt(ZonedDateTimeFilter beginDt) {
        this.beginDt = beginDt;
    }

    public ZonedDateTimeFilter getDueDt() {
        return dueDt;
    }

    public ZonedDateTimeFilter dueDt() {
        if (dueDt == null) {
            dueDt = new ZonedDateTimeFilter();
        }
        return dueDt;
    }

    public void setDueDt(ZonedDateTimeFilter dueDt) {
        this.dueDt = dueDt;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserCourseCriteria that = (UserCourseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(courseType, that.courseType) &&
            Objects.equals(name, that.name) &&
            Objects.equals(url, that.url) &&
            Objects.equals(beginDt, that.beginDt) &&
            Objects.equals(dueDt, that.dueDt) &&
            Objects.equals(userId, that.userId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, courseType, name, url, beginDt, dueDt, userId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserCourseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (courseType != null ? "courseType=" + courseType + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (beginDt != null ? "beginDt=" + beginDt + ", " : "") +
            (dueDt != null ? "dueDt=" + dueDt + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }
}
