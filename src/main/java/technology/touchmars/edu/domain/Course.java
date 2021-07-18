package technology.touchmars.edu.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import technology.touchmars.edu.domain.enumeration.CourseType;

/**
 * A Course.
 */
@Table("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("code")
    private String code;

    @Column("course_type")
    private CourseType courseType;

    @Column("name")
    private String name;

    @Column("jhi_desc")
    private String desc;

    @Column("url")
    private String url;

    @Column("cover")
    private byte[] cover;

    @Column("cover_content_type")
    private String coverContentType;

    @Column("version")
    private String version;

    @Column("created_dt")
    private ZonedDateTime createdDt;

    @Column("created_by")
    private String createdBy;

    @Column("start_dt")
    private ZonedDateTime startDt;

    @Column("end_dt")
    private ZonedDateTime endDt;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course id(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public Course code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CourseType getCourseType() {
        return this.courseType;
    }

    public Course courseType(CourseType courseType) {
        this.courseType = courseType;
        return this;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getName() {
        return this.name;
    }

    public Course name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public Course desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return this.url;
    }

    public Course url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getCover() {
        return this.cover;
    }

    public Course cover(byte[] cover) {
        this.cover = cover;
        return this;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return this.coverContentType;
    }

    public Course coverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
        return this;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public String getVersion() {
        return this.version;
    }

    public Course version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ZonedDateTime getCreatedDt() {
        return this.createdDt;
    }

    public Course createdDt(ZonedDateTime createdDt) {
        this.createdDt = createdDt;
        return this;
    }

    public void setCreatedDt(ZonedDateTime createdDt) {
        this.createdDt = createdDt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Course createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getStartDt() {
        return this.startDt;
    }

    public Course startDt(ZonedDateTime startDt) {
        this.startDt = startDt;
        return this;
    }

    public void setStartDt(ZonedDateTime startDt) {
        this.startDt = startDt;
    }

    public ZonedDateTime getEndDt() {
        return this.endDt;
    }

    public Course endDt(ZonedDateTime endDt) {
        this.endDt = endDt;
        return this;
    }

    public void setEndDt(ZonedDateTime endDt) {
        this.endDt = endDt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return id != null && id.equals(((Course) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", courseType='" + getCourseType() + "'" +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            ", url='" + getUrl() + "'" +
            ", cover='" + getCover() + "'" +
            ", coverContentType='" + getCoverContentType() + "'" +
            ", version='" + getVersion() + "'" +
            ", createdDt='" + getCreatedDt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", startDt='" + getStartDt() + "'" +
            ", endDt='" + getEndDt() + "'" +
            "}";
    }
}
