package technology.touchmars.edu.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A UserProfileInterest.
 */
@Table("user_profile_interest")
public class UserProfileInterest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("code")
    private String code;

    @Transient
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private UserProfile userProfile;

    @Transient
    private Interest interest;

    @Column("user_profile_id")
    private Long userProfileId;

    @Column("interest_id")
    private Long interestId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfileInterest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public UserProfileInterest code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.userProfileId = userProfile != null ? userProfile.getId() : null;
    }

    public UserProfileInterest userProfile(UserProfile userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    public Interest getInterest() {
        return this.interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
        this.interestId = interest != null ? interest.getId() : null;
    }

    public UserProfileInterest interest(Interest interest) {
        this.setInterest(interest);
        return this;
    }

    public Long getUserProfileId() {
        return this.userProfileId;
    }

    public void setUserProfileId(Long userProfile) {
        this.userProfileId = userProfile;
    }

    public Long getInterestId() {
        return this.interestId;
    }

    public void setInterestId(Long interest) {
        this.interestId = interest;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfileInterest)) {
            return false;
        }
        return id != null && id.equals(((UserProfileInterest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileInterest{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            "}";
    }
}
